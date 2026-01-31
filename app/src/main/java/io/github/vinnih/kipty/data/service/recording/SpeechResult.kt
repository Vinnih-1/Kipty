package io.github.vinnih.kipty.data.service.recording

import io.github.vinnih.kipty.data.transcriptor.Transcriptor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpeechResult @Inject constructor(private val transcriptor: Transcriptor) {

    private suspend fun transcript(floatArray: FloatArray) = transcriptor.transcribe(floatArray)

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

    suspend fun calculatePronunciationScore(
        expected: String,
        floatArray: FloatArray
    ): Pair<String, Int> {
        transcriptor.initialize()

        val transcription = transcript(floatArray)

        val distance = levenshteinDistance(
            expected.lowercase().trim(),
            transcription.lowercase().trim()
        )
        val maxLength = maxOf(expected.length, transcription.length)
        val similarity = (1 - (distance.toFloat() / maxLength)) * 100

        return transcription to similarity.toInt()
    }
}
