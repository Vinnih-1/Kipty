package io.github.vinnih.kipty.data.service.record

import io.github.vinnih.kipty.data.service.transcript.TranscriptorService
import io.github.vinnih.kipty.utils.convertTranscription
import java.io.File
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
        usedIndices: MutableSet<Int>,
        lastMatchIndex: Int
    ): Pair<Int, Float> {
        var bestIndex = -1
        var bestSimilarity = 0f

        transcribedWords.forEachIndexed { index, transcribedWord ->
            if (index !in usedIndices) {
                val similarity = wordSimilarity(expectedWord, transcribedWord)

                val proximityBonus = if (index > lastMatchIndex) 0.05f else 0f
                val effectiveSimilarity = similarity + proximityBonus

                if (effectiveSimilarity > bestSimilarity) {
                    bestSimilarity = effectiveSimilarity

                    if (similarity > 0.7f) {
                        bestIndex = index
                    }
                }
            }
        }

        val finalSimilarity = if (bestIndex != -1) {
            wordSimilarity(expectedWord, transcribedWords[bestIndex])
        } else {
            0f
        }

        return bestIndex to finalSimilarity
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
        var lastMatchIndex = -1

        expectedWords.forEach { expectedWord ->
            val (bestIndex, similarity) = findBestMatch(
                expectedWord,
                transcribedWords,
                usedIndices,
                lastMatchIndex
            )

            if (bestIndex != -1) {
                usedIndices.add(bestIndex)
                lastMatchIndex = bestIndex
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
        audioFile: File,
        onScore: (DetailedPronunciationResult) -> Unit
    ) {
        val rawJson = transcriptor.recognizeFile(
            audioFile = audioFile,
            onFailure = { it.printStackTrace() }
        )
        val text = rawJson.convertTranscription().joinToString { it.text }

        onScore(evaluateDetailed(expected, text))
    }
}
