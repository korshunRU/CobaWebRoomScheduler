package ru.korshun.cobawebroomscheduler;

import java.util.ArrayList;
import java.util.Arrays;

public final class Config {


    /**
    *   Данные для доступа к БД mysql
    */
    public static final String HOST_SUFFIX =        "jdbc:mysql://";
    public static final String HOST =               "localhost:3306/";
    public static final String DATABASE_NAME =      "coba_web_room";
    public static final String USERNAME =           "coba";
    public static final String PASSWORD =           "coba";
    public static final String DB_TABLE_PREFIX =    "coba_";



    /**
     *   Данные для доступа к БД access
     */
    public static final String PATH =               "res/";
    public static final String A_DATABASE_NAME =    "Cab.mdb";



    /**
    *   Массив символов для кодирования
    */
    public final static ArrayList<String> WORDS_LIST = new ArrayList<>(Arrays.asList(
            "а", "б", "в", "г", "д", "е", "ё", "ж", "з", "и", "й", "к", "л", "м", "н", "о", "п", "р", "с", "т", "у", "ф", "х", "ц", "ч", "ш", "щ", "ъ", "ы", "ь", "э", "ю", "я",
            "А", "Б", "В", "Г", "Д", "Е", "Ё", "Ж", "З", "И", "Й", "К", "Л", "М", "Н", "О", "П", "Р", "С", "Т", "У", "Ф", "Х", "Ц", "Ч", "Ш", "Щ", "Ъ", "Ы", "Ь", "Э", "Ю", "Я",
            "q", "w", "e", "r", "t", "y", "u", "i", "o", "p", "a", "s", "d", "f", "g", "h", "j", "k", "l", "z", "x", "c", "v", "b", "n", "m",
            "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P", "A", "S", "D", "F", "G", "H", "J", "K", "L", "Z", "X", "C", "V", "B", "N", "M",
            " ", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "-", "_", "(", ")", "\"", "/", ",", ".", ":", ";", "'", "!", "=", "+", "\\", "@", "#", "№", "$", "%", "^", "&", "?", "*", "~", "`"));



    /**
    *   Разделитель символов при кодировании
    */
    public final static String WORDS_DIVIDER = "$";


}
