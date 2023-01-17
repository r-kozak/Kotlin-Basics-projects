fun main() {
    while (true) {
        print("Enter two numbers in format: {source base} {target base} (To quit type /exit) ")
        val s = readLine()!!
        if (s == "/exit") break
        val (from, to) = s.split(' ').map { it.toInt() }
        while (true) {
            print("Enter number in base $from to convert to base $to (To go back type /back) ")
            val buf = readLine()!!.split('.')
            if (buf[0] == "/back") break
            print("Conversion result: ${buf[0].toBigInteger(from).toString(to)}")
            if (buf.size == 2) {
                print('.')
                var tail = 0.0
                for (d in buf[1].reversed()) tail = (tail + d.toString().toInt(36)) / from
                repeat(5) {
                    tail *= to
                    print(tail.toInt().toString(36))
                    tail %= 1.0
                }
            }
            println()
        }
    }
}