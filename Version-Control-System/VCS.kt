package svcs

import java.io.File

private var configFile = File(VcsConstants.CONFIG_FILE_NAME)
private var indexFile = File(VcsConstants.INDEX_FILE_NAME)
private var logFile = File(VcsConstants.LOG_FILE_NAME)
private var commitsDir = File("")

private val commands = mapOf(
    "config" to "Get and set a username.",
    "add" to "Add a file to the index.",
    "log" to "Show commit logs.",
    "commit" to "Save changes.",
    "checkout" to "Restore a file."
)

fun main(args: Array<String>) {
    prepareVcsDir()

    val command = args.getOrNull(0)
    val outputText = if (command == VcsConstants.HELP_COMMAND || command == null) {
        getEntireHelp()
    } else {
        commands.getOrDefault(command, "'$command' ${VcsConstants.WRONG_COMMAND_TXT}")
    }
    val arg = args.getOrNull(1)

    when (command) {
        "config" -> config(arg)
        "add" -> add(arg)
        "log" -> log()
        "commit" -> commit(arg)
        "checkout" -> checkout(arg)
        else -> println(outputText)
    }
}

fun checkout(commitId: String?) {
    if (commitId == null) {
        println(VcsConstants.NO_COMMIT_ID)
    } else if (!commitsDir.resolve(commitId).exists()) {
        println(VcsConstants.COMMIT_NOT_EXISTS)
    } else {
        replaceFilesFromCommit(commitId)
        println("Switched to commit $commitId.")
    }
}

fun replaceFilesFromCommit(commitDirName: String) {
    commitsDir.resolve(commitDirName).listFiles().forEach {
        it.copyTo(File(it.name), true)
    }
}

fun isIndexedFilesChanged(indexedFilesHash: String): Boolean = getLastCommitHash() != indexedFilesHash

fun getLastCommitHash(): String {
    val commitsHistory = logFile.readLines()
    return if (commitsHistory.isEmpty()) {
        ""
    } else {
        commitsHistory.first().split(" ")[1]
    }
}

fun commit(commitMsg: String?) {
    if (commitMsg == null) {
        println(VcsConstants.MSG_NOT_PASSED)
    } else {
        val indexedFilesHash = computeIndexedFilesHash()
        if (isIndexedFilesChanged(indexedFilesHash)) {
            makeCommit(commitMsg, indexedFilesHash)
        } else {
            println(VcsConstants.NOTHING_TO_COMMIT)
        }
    }
}

fun computeIndexedFilesHash(): String {
    val indexFileContent = indexFile.readLines()
    if (indexFileContent.isEmpty()) {
        return ""
    }
    val filesBytes = mutableListOf<Byte>()
    for(filePath in indexFileContent) {
        val file = File(filePath)
        if (file. exists()) {
            filesBytes.addAll(file.readBytes().toList())
        }
    }
    return Sha256Calculator.hashBytes(filesBytes.toByteArray())
}

fun makeCommit(commitMsg: String, indexedFilesHash: String) {
    writeCommitToLog(indexedFilesHash, commitMsg)
    copyIndexedFiles(indexedFilesHash)
    println(VcsConstants.CHANGES_COMMITTED)
}

fun writeCommitToLog(commitHash: String, commitMsg: String) {
    val initialLogText = logFile.readText()
    val newCommitText = """
    commit $commitHash
    Author: ${getCurrentUserName()}
    $commitMsg
    """.trimIndent()

    logFile.writeText("$newCommitText\n\n$initialLogText")
}

fun copyIndexedFiles(newFolderName: String) {
    val newFolder = commitsDir.resolve(newFolderName)
    if (!newFolder.exists()) newFolder.mkdir()
    for(filePath in indexFile.readLines()) {
        val srcFile = File(filePath)
        if (srcFile. exists()) {
            val targetFile = newFolder.resolve(srcFile.name)
            srcFile.copyTo(targetFile, true)
        }
    }
}

fun log() {
    println(logFile.readText().ifBlank { VcsConstants.NO_COMMITS_MSG })
}

fun add(filename: String?) {
    if (filename == null) {
        val indexContent = indexFile.readText()
        println(if (indexContent.isBlank()) {
            VcsConstants.ADD_FILE_MSG
        } else {
            "Tracked files:\n$indexContent"
        })
    } else {
        if (File(filename).exists()) {
            indexFile.appendText("$filename\n")
            println("The file '$filename' is tracked.")
        } else {
            println("Can't find '$filename'.")
        }
    }
}

fun config(newUserName: String?) {
    if (newUserName == null) {
        val userName = getCurrentUserName()
        println(if (userName.isBlank()) VcsConstants.WHO_YOU_ARE else "${VcsConstants.USER_NAME_IS} $userName.")
    } else {
        configFile.writeText(newUserName)
        println("${VcsConstants.USER_NAME_IS} $newUserName.")
    }
}

private fun getCurrentUserName() = configFile.readText()

fun prepareVcsDir() {
    // create a vcs dir if doesn't exist
    val vcsDir = File(VcsConstants.VCS_DIR_NAME)
    if (!vcsDir.exists()) vcsDir.mkdir()

    // create vcs/commits dir if doesn't exist
    commitsDir = vcsDir.resolve(VcsConstants.COMMITS_DIR_NAME)
    if (!commitsDir.exists()) commitsDir.mkdir()

    // create required files
    configFile = vcsDir.resolve(configFile)
    if (!configFile.exists()) configFile.writeText("")

    indexFile = vcsDir.resolve(indexFile)
    if (!indexFile.exists()) indexFile.writeText("")

    logFile = vcsDir.resolve(logFile)
    if (!logFile.exists()) logFile.writeText("")
}

private fun getEntireHelp(): String {
    var result = "These are SVCS commands:\n"
    commands.forEach { (k, v) -> result += "${k.padEnd(VcsConstants.COMMAND_NAME_LENGTH, ' ')}$v\n"}
    return result
}
