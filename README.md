# Miduino
Парсер `*.mid` файлов и генератор кода проигрывания этих мелодий
на ардуино.

#### Требования
* JDK 1.8+
* [Gradle 4.3+](https://gradle.org/install/)
* ~~log4j 1.2.17~~ (gradle все решит)

#### Установка и запуск
Linux:<br>
`git clone https://github.com/Xenoseus/miduino.git`<br>
`cd miduino`<br>
`cp path/to/file.mid input.mid`<br>
`gradle run`

Если вся мелодия не помещается в терминал,
то отредактируйте `src/main/resources/log4j.properties`:<br>
`log4j.appender.file.File=абсолютный_путь_к_логу.log`<br>

Windows:<br>
`clone or download -> download zip`<br>
*Распакуйте архив, откройте командную строку из папки miduino-master и скопируйте в эту директорию Ваш файл как input.mid*<br>
*Запустите* `gradle run`<br>

#### Текущие задачи
- [x] Проиграть ноты разных частот на arduino. Спасибо [tagliati](https://gist.github.com/tagliati/1804108)
- [x] Пропарсить *.mid, достать оттуда ноты
- [x] Поделить их на мнимые каналы (параллельно идущие ноты в одном канале)
- [x] Сформировать генераторы arduino-кода на основании нот и таблицу частот этих нот
- [ ] Реализовать проигрывание нескольких звуковых каналов одновременно
- [ ] Обработать не обработанные события midi
- [ ] Сделать графическую оболочку для miduino
- [ ] ~~Вовлечь в разработку остальных членов команды~~