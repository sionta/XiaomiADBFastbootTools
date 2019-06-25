import javafx.scene.control.TextInputControl
import java.io.File
import java.util.*

open class Command {

    companion object {
        var pb: ProcessBuilder = ProcessBuilder()
        var prefix = ""
        var output = ""
        lateinit var tic: TextInputControl
        lateinit var proc: Process

        init {
            pb.directory(File(System.getProperty("user.dir")))
        }

        fun setup(pref: String): Boolean {
            prefix = pref
            return try {
                pb.command("${prefix}adb", "--version").start()
                pb.command("${prefix}fastboot", "--version").start()
                pb.command("${prefix}adb", "start-server").start()
                true
            } catch (e: Exception) {
                false
            }
        }

        fun exec(vararg args: String, lim: Int = 0, err: Boolean = true): String {
            pb.redirectErrorStream(err)
            output = ""
            args.forEach {
                proc = pb.command((prefix + it).split(' ', limit = lim)).start()
                val scan = Scanner(proc.inputStream, "UTF-8").useDelimiter("")
                while (scan.hasNextLine())
                    output += scan.nextLine() + '\n'
                scan.close()
                proc.waitFor()
            }
            return output
        }

        fun exec_displayed(vararg args: String, lim: Int = 0, err: Boolean = true): String {
            pb.redirectErrorStream(err)
            output = ""
            tic.text = ""
            args.forEach {
                proc = pb.command((prefix + it).split(' ', limit = lim)).start()
                val scan = Scanner(proc.inputStream, "UTF-8").useDelimiter("")
                var next: String
                while (scan.hasNextLine()) {
                    next = scan.nextLine() + '\n'
                    output += next
                    tic.appendText(next)
                }
                scan.close()
                proc.waitFor()
            }
            return output
        }
    }
}