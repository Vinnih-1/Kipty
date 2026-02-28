package io.github.vinnih.kipty.data.service.record

import io.github.vinnih.kipty.data.service.transcriptor.TranscriptorService
import javax.inject.Inject
import javax.inject.Singleton

data class WordScore(val word: String, val isCorrect: Boolean, val similarity: Float)

data class DetailedPronunciationResult(
    val transcription: String,
    val overallScore: Int,
    val wordScores: List<WordScore>,
    val correctWords: Int,
    val totalWords: Int
)

@Singleton
class SpeechResult @Inject constructor(private val transcriptor: TranscriptorService) {

    private suspend fun transcript(floatArray: FloatArray) = transcriptor.transcribe(floatArray)

    private fun normalizeText(text: String): String = text.lowercase()
        .replace(Regex("[,.:;!?\"'-]"), "")
        .replace(Regex("\\s+"), " ")
        .trim()

    private fun wordSimilarity(word1: String, word2: String): Float {
        if (word1 == word2) return 1f

        val distance = levenshteinDistance(word1, word2)
        val maxLength = maxOf(word1.length, word2.length)

        if (maxLength == 0) return 1f

        return 1f - (distance.toFloat() / maxLength)
    }

    private fun levenshteinDistance(s1: String, s2: String): Int {
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }

        for (i in 0..s1.length) dp[i][0] = i
        for (j in 0..s2.length) dp[0][j] = j

        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,
                    dp[i][j - 1] + 1,
                    dp[i - 1][j - 1] + cost
                )
            }
        }

        return dp[s1.length][s2.length]
    }

    private fun findBestMatch(
        expectedWord: String,
        transcribedWords: List<String>,
        usedIndices: MutableSet<Int>
    ): Pair<Int, Float> {
        var bestIndex = -1
        var bestSimilarity = 0f

        transcribedWords.forEachIndexed { index, transcribedWord ->
            if (index !in usedIndices) {
                val similarity = wordSimilarity(expectedWord, transcribedWord)
                if (similarity > bestSimilarity) {
                    bestSimilarity = similarity
                    bestIndex = index
                }
            }
        }

        return bestIndex to bestSimilarity
    }

    private fun evaluateDetailed(
        expected: String,
        transcribed: String
    ): DetailedPronunciationResult {
        val expectedNormalized = normalizeText(expected)
        val transcribedNormalized = normalizeText(transcribed)

        val expectedWords = expectedNormalized.split(" ")
        val transcribedWords = transcribedNormalized.split(" ")

        val wordScores = mutableListOf<WordScore>()
        val usedIndices = mutableSetOf<Int>()
        var totalSimilarity = 0f

        expectedWords.forEach { expectedWord ->
            val (bestIndex, similarity) = findBestMatch(
                expectedWord,
                transcribedWords,
                usedIndices
            )

            if (bestIndex != -1) {
                usedIndices.add(bestIndex)
            }

            val isCorrect = similarity >= 0.8f

            wordScores.add(
                WordScore(
                    word = expectedWord,
                    isCorrect = isCorrect,
                    similarity = similarity
                )
            )

            totalSimilarity += similarity
        }

        val correctWords = wordScores.count { it.isCorrect }
        val overallScore = ((totalSimilarity / expectedWords.size) * 100)
            .toInt()
            .coerceIn(0, 100)

        return DetailedPronunciationResult(
            transcription = transcribed,
            overallScore = overallScore,
            wordScores = wordScores,
            correctWords = correctWords,
            totalWords = expectedWords.size
        )
    }

    suspend fun calculatePronunciationScore(
        expected: String,
        byteArray: ByteArray
    ): Pair<String, Int> {
        transcriptor.initialize()

        val transcription = transcript(transcriptor.normalizeAudio(byteArray))
        val result = evaluateDetailed(expected, transcription)

        return transcription to result.overallScore
    }
}
