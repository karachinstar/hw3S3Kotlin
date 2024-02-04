/* Домашнее задание 1
Написать программу, которая обрабатывает введённые пользователем в консоль команды:
exit
help
add <Имя> phone <Номер телефона>
add <Имя> email <Адрес электронной почты>

После выполнения команды, кроме команды exit, программа ждёт следующую команду.

Имя – любое слово.
Если введена команда с номером телефона, нужно проверить, что указанный телефон может начинаться с +, затем идут только цифры.
При соответствии введённого номера этому условию – выводим его на экран вместе с именем, используя строковый шаблон.
В противном случае - выводим сообщение об ошибке.
Для команды с электронной почтой делаем то же самое, но с другим шаблоном – для простоты, адрес должен содержать три
последовательности букв, разделённых символами @ и точкой.

Пример команд:
add Tom email tom@example.com
add Tom phone +7876743558
 */

/*Домашнее задание 2
За основу берём код решения домашнего задания из предыдущего семинара и дорабатываем его.

— Создайте иерархию sealed классов, которые представляют собой команды. В корне иерархии интерфейс Command.
— В каждом классе иерархии должна быть функция isValid(): Boolean, которая возвращает true, если команда
введена с корректными аргументами. Проверку телефона и email нужно перенести в эту функцию.
— Напишите функцию readCommand(): Command, которая читает команду из текстового ввода, распознаёт её и
возвращает один из классов-наследников Command, соответствующий введённой команде.
— Создайте data класс Person, который представляет собой запись о человеке. Этот класс должен содержать поля:
name – имя человека
phone – номер телефона
email – адрес электронной почты
— Добавьте новую команду show, которая выводит последнее значение, введённой с помощью команды add.
Для этого значение должно быть сохранено в переменную типа Person. Если на момент выполнения команды show не
было ничего введено, нужно вывести на экран сообщение “Not initialized”.
— Функция main должна выглядеть следующем образом. Для каждой команды от пользователя:
Читаем команду с помощью функции readCommand
Выводим на экран получившийся экземпляр Command
Если isValid для команды возвращает false, выводим help. Если true, обрабатываем команду внутри when.
 */

/*Домашнее задание 3
Продолжаем дорабатывать домашнее задание из предыдущего семинара. За основу берём код решения из предыдущего домашнего
задания.

— Измените класс Person так, чтобы он содержал список телефонов и список почтовых адресов, связанных с человеком.
— Теперь в телефонной книге могут храниться записи о нескольких людях. Используйте для этого наиболее подходящую
структуру данных.
— Команда AddPhone теперь должна добавлять новый телефон к записи соответствующего человека.
— Команда AddEmail теперь должна добавлять новый email к записи соответствующего человека.
— Команда show должна принимать в качестве аргумента имя человека и выводить связанные с ним телефоны и адреса
электронной почты.
— Добавьте команду find, которая принимает email или телефон и выводит список людей, для которых записано такое
значение.

 */
val phoneAndMailBook = mutableMapOf<String, Person>()
sealed interface Command {
    fun isValid(): Boolean
}

data class AddPhone(val name: String, val phone: String) : Command {
    override fun isValid() = phone.matches(Regex("""^\+?\d+${'$'}"""))
}


data class AddEmail(val name: String, val email: String) : Command {
    override fun isValid() = email.matches(Regex("""^[A-Za-z\d](.*)(@)(.+)(\.)([A-Za-z]{2,})"""))
}

object Exit : Command {
    override fun isValid() = true
}

object Help : Command {
    override fun isValid() = true
}

data class Show(val name: String) : Command {
    override fun isValid() = true
}

data class Find(val info: String) : Command {
    override fun isValid() = true
}

// Класс Person
data class Person(
    var name: String,
    var phones: MutableList<String> = mutableListOf(),
    var emails: MutableList<String> = mutableListOf()
)


fun readCommand(input: String): Command {
    val parts = input.split(" ")
    // Распознавание команды
    return when (parts[0]) {
        "add" -> {
            if (parts.size == 4) {
                when (parts[2]) {
                    "phone" -> AddPhone(parts[1], parts[3])
                    "email" -> AddEmail(parts[1], parts[3])
                    else -> Help
                }
            } else {
                return Help
            }
        }

        "exit" -> Exit
        "help" -> Help
        "show" -> Show(parts[1])
        "find" -> Find(parts[1])
        else -> {
            println("Неизвестная команда")
            Help
        }
    }
}

fun main(){
    println("""Добро пожаловать в записную книжку
    Список команд:
    exit - выход
    help - справка
    add <Имя> phone <Номер телефона> - Добавить имя человека и номер телефона(только цифры(может начинаться с +)
    add <Имя> email <Адрес электронной почты> - Добавить имя человека и mail(обязательно в адресе содержит @ и .)
    show <Имя> - показать контакты данного человека
    find <phone> или <mail> - показать людей с данным контакто или именем""")
    //var person: Person? = null
    while (true) {
        print("Введите команду: ")
        val command = readCommand(readLine()!!.lowercase())
        if (command.isValid()) {
            when (command) {
                is AddPhone -> phoneAndMailBook.getOrPut(command.name) { Person(command.name) }.also {
                    it.phones.add(command.phone)
                    println("Добавлено: Имя: ${it.name}, телефон: ${command.phone}")
                }

                is AddEmail -> phoneAndMailBook.getOrPut(command.name) { Person(command.name) }.also {
                    it.emails.add(command.email)
                    println("Добавлено: Имя: ${it.name}, email: ${command.email}")
                }

                is Show -> {
                    phoneAndMailBook[command.name]?.let {
                        println(
                            "Имя: ${it.name}; \n" +
                                    "Телефоны: ${it.phones.joinToString()}; \n" +
                                    "Emails: ${it.emails.joinToString()}"
                        )
                    } ?: println("Запись не найдена")
                }

                is Find -> {
                    phoneAndMailBook.values.filter {
                        it.phones.contains(command.info)
                                || it.emails.contains(command.info)
                    }.takeIf { it.isNotEmpty() }?.forEach {
                        println(
                            "Имя: ${it.name}; \n" +
                                    "Телефоны: ${it.phones.joinToString()}; \n" +
                                    "Emails: ${it.emails.joinToString()}"
                        )
                    } ?: println("Записи не найдены")
                }

                is Help -> {
                    println("""Список команд:
    exit - выход
    help - справка
    add <Имя> phone <Номер телефона> - Добавить имя человека и номер телефона(только цифры(может начинаться с +)
    add <Имя> email <Адрес электронной почты> - Добавить имя человека и mail(обязательно в адресе содержит @ и .)
    show - показать последнюю добавленную запись""")
                }
                is Exit -> return
                else -> println("Неизвестная команда")
            }
        }
    }
}
