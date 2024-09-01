import kotlinx.coroutines.*
import kotlinx.coroutines.flow.asFlow
import kotlin.random.Random
import kotlin.system.measureTimeMillis

@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
suspend fun main() {
    val passList: MutableList<String> = mutableListOf()
    val idList: MutableList<String> = mutableListOf()
    println("Сколько пользователей нужно создать?")
    val users = readlnOrNull()?.toInt()
    println("C какого символа должен начинаться пароль?")
    val symbol = readlnOrNull().toString()

    val time = measureTimeMillis {
        withContext(newSingleThreadContext("users_thread_context")) {
            launch {
                if (users != null) {
                    getIdFlow(users).collect { i ->
//                        println("ID: $i")
                        idList.add(i)
                    }
                }
            }
            launch {
                if (users != null) {
                    getPasswordFlow(symbol, users).collect { i ->
//                        println("PASS: $i")
                        passList.add(i)
                    }
                }
            }
        }
    }
    val finishList = idList.zip(passList).toMap()
    println(finishList)
//    println(idList)
//    println(passList)
    println("Общее затраченное время: $time мс")

}

fun createPassword(): String {
    val chars = "abcdefghijklmnopqrstuvwxyz1234567890".toCharArray()
    var password = ""
    for (i in 0..<6) {
        val randomIndex = Random.nextInt(chars.size)
        password = if (i % 2 == 0) {
            "$password${(chars[randomIndex]).uppercaseChar()}"
        } else {
            "$password${(chars[randomIndex])}"
        }
    }
    return password
}

fun getListOfPassword(input: String, count: Int): List<String> {
    var position = 0
    val list: MutableList<String> = mutableListOf()
    while (true) {
        if (position < count) {
            val pass = createPassword()
            if (pass.first().toString().toUpperCase() == (input.toUpperCase())) {
                list.add(pass)
                position++
            }
        } else break
    }
    return list.toList()
}

fun getListId(count: Int): List<String> {
    var number = 0
    var numberString: String
    val list = mutableListOf<String>()
    for (i in 0..<count) {
        number++
        numberString = number.toString().padStart(6, '0')
        list.add(numberString)
    }
    return list.toList()
}

fun getIdFlow(count: Int) = getListId(count).asFlow()

fun getPasswordFlow(input: String, count: Int) = getListOfPassword(input, count).asFlow()
