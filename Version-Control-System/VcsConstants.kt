package svcs

object VcsConstants {
    // checkout command messages
    const val NO_COMMIT_ID = "Commit id was not passed."
    const val COMMIT_NOT_EXISTS = "Commit does not exist."
    // log and commit commands messages
    const val MSG_NOT_PASSED = "Message was not passed."
    const val CHANGES_COMMITTED = "Changes are committed."
    const val NOTHING_TO_COMMIT = "Nothing to commit."
    const val NO_COMMITS_MSG = "No commits yet."
    // config and add commands messages
    const val WHO_YOU_ARE = "Please, tell me who you are."
    const val USER_NAME_IS = "The username is"
    const val ADD_FILE_MSG = "Add a file to the index."
    // file names
    const val VCS_DIR_NAME = "vcs"
    const val COMMITS_DIR_NAME = "commits"
    const val CONFIG_FILE_NAME = "config.txt"
    const val INDEX_FILE_NAME = "index.txt"
    const val LOG_FILE_NAME = "log.txt"
    // help messages
    const val COMMAND_NAME_LENGTH = 11
    const val WRONG_COMMAND_TXT: String = " is not a SVCS command."
    const val HELP_COMMAND = "--help"
    // other constants
    const val SHA_ALGORITHM = "SHA-256"
}
