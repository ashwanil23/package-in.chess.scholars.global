package `in`.chess.scholars.global.engine

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*

class StockfishEngine(private val context: Context) {

    private var process: Process? = null
    private var reader: BufferedReader? = null
    private var writer: BufferedWriter? = null

    /**
     * Starts the Stockfish process from the app's assets.
     */
    suspend fun start() {
        withContext(Dispatchers.IO) {
            try {
                // Determine the correct binary for the device's architecture
                val arch = System.getProperty("os.arch")
                val stockfishAssetPath = when {
                    arch?.contains("aarch64") == true -> "arm64-v8a/stockfish"
                    arch?.contains("x86_64") == true -> "x86_64/stockfish"
                    else -> "arm64-v8a/stockfish" // Default fallback
                }

                // Copy the engine from assets to a private, executable file location
                val executableFile = File(context.filesDir, "stockfish")
                context.assets.open(stockfishAssetPath).use { assetInputStream ->
                    FileOutputStream(executableFile).use { fileOutputStream ->
                        assetInputStream.copyTo(fileOutputStream)
                    }
                }
                executableFile.setExecutable(true)

                // Start the process
                process = ProcessBuilder(executableFile.absolutePath).start()
                reader = BufferedReader(InputStreamReader(process!!.inputStream))
                writer = BufferedWriter(OutputStreamWriter(process!!.outputStream))

                // Initialize UCI (Universal Chess Interface) mode and wait for confirmation
                sendCommand("uci")
                while (reader?.readLine() != "uciok") { /* Wait for engine to be ready */ }

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun sendCommand(command: String) {
        writer?.write("$command\n")
        writer?.flush()
    }

    /**
     * Finds the best move for a given board state.
     * @param fen The board state in Forsyth-Edwards Notation (FEN).
     * @param moveTimeMillis The time the engine should think.
     * @return The best move in long algebraic notation (e.g., "e2e4").
     */
    suspend fun findBestMove(fen: String, moveTimeMillis: Long = 1500): String? {
        return withContext(Dispatchers.IO) {
            sendCommand("position fen $fen")
            sendCommand("go movetime $moveTimeMillis")

            var output: String?
            var bestMove: String? = null
            while (reader?.readLine().also { output = it } != null) {
                if (output?.startsWith("bestmove") == true) {
                    bestMove = output?.split(" ")?.get(1)
                    break
                }
            }
            bestMove
        }
    }

    /**
     * Stops the engine process to release resources.
     */
    fun stop() {
        process?.destroy()
        process = null
    }
}