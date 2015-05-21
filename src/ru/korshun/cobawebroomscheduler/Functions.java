package ru.korshun.cobawebroomscheduler;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * Created by user on 19.01.2015.
 */
public final class Functions {

    /*
    *   Кодирование (шифрование) строки
    */
    public static String encodeStr(String str) {
        if(str != null && str.length() > 0) {
            String encodeString = "";
            for (int x = 0; x < str.length(); x++) {
                encodeString += Config.WORDS_LIST.indexOf(Character.toString(str.charAt(x))) + Config.WORDS_DIVIDER;
            }
            return encodeString;
        }
        return null;
    }

    /*
    *   Декодирование(дешифрование) строки
    */
    public static String decodeStr(String str) {
        if(str != null && str.length() > 0) {
            String decodeString = "", helpStr = "";
            for (int x = 0; x < str.length(); x++) {
                if (!Character.toString(str.charAt(x)).equals(Config.WORDS_DIVIDER)) {
                    helpStr += Character.toString(str.charAt(x));
                } else {
                    decodeString += Config.WORDS_LIST.get(Integer.parseInt(helpStr));
                    helpStr = "";
                }
            }
            return decodeString;
        }
        return null;
    }

//    /*
//    *   Функция получения md5 хеша заданной строки
//    */
//    public static String md5(final String text){
//        try{
//            MessageDigest md = MessageDigest.getInstance("MD5");
//            md.update(text.getBytes());
//            String hash = new BigInteger(1, md.digest()).toString(16);
//            while(hash.length() < 32) { hash = "0" + hash; }
//
//            return hash;
//        } catch(NoSuchAlgorithmException e){
////            new ExceptionDialog(e).show();
//        }
//
//        return null;
//    }



//    /*
//    *   Получаем имя файла без расширения из объекта типа URL
//    */
//    public static String getFileNameFromFullPath(String path) {
//        return path.substring(path.lastIndexOf('/') + 1, path.lastIndexOf('.'));
//    }





//    /**
//     *      Создание диалогового окна с ошибкой
//     * @param error             - текст сообщения об ошибке
//     * @param exceptionString   - имя класса Throwable (%Throwable%.getClass().toString)
//     * @param rootStage         - опциональный параметр: если true - то при закрытии убиваем приложение
//     */
//    public static void showExceptionDialog(String error, String exceptionString, boolean ... rootStage) {
//        ExceptionDialog ed = new ExceptionDialog(new Exception(exceptionString));
//        ed.setHeaderText(error);
//        ed.setTitle("Ошибка");
//        ed.initModality(Modality.APPLICATION_MODAL);
//        ed.show();
//        if(rootStage.length > 0 && rootStage[0]) {
//            ed.setOnCloseRequest(e -> {
//                System.exit(0);
//                Platform.exit();
//            });
//        }
//    }

//
//    /**
//     *      Создание панели нотификации (выезжающее окно сверху)
////     * @param text      - текст в окне
////     * @param delay     - пауза перед закрытием, сек.
////     * @param type      - тип окна (ошибка, уведомление и т.д.)
//     */
//    public static void showNotification(String text, double delay, NotificationType type) {
//        Notifications notificationBuilder =
//                Notifications.create()
//                        .title(" ")
//                        .text(text)
//                        .hideAfter(Duration.seconds(delay))
//                        .position(Pos.TOP_CENTER)
//                        .hideCloseButton();
//        switch (type) {
//            case WARNING:       notificationBuilder.showWarning(); break;
//            case INFO:          notificationBuilder.showInformation(); break;
//            case CONFIRM:       notificationBuilder.showConfirm(); break;
//            case ERROR:         notificationBuilder.showError(); break;
//            default:            notificationBuilder.show();
//        }
//    }

//    public enum NotificationType {
//
//        WARNING,
//
//        INFO,
//
//        CONFIRM,
//
//        ERROR
//
//    }



    /**
    *   Функция парсинга строки в дату для записи в БД
    *   string - дата из поля формы
    *   format - формат даты для вывода (напр. "yyyy-MM-dd HH:mm:ss")
    */
    public static String parseStringToSqlDateFormat(Object string, String format) throws ParseException {
        if(string != null && string.toString().length() > 0) {
//            System.out.println(new SimpleDateFormat(format).format(new SimpleDateFormat(format).parse(string.toString())));
            return new SimpleDateFormat(format).format(new SimpleDateFormat(format).parse(string.toString()));
        }
        return null;
    }




    /**
     *      Вывод текущей даты\времени в заданном формате
     * @param outputFormat            - формат для вывода ("yyyy/MM/dd HH:mm:ss")
     */
    public static String getDateTime(String outputFormat) {
        return new SimpleDateFormat(outputFormat).format(Calendar.getInstance().getTime());
    }


    /**
     *      Проверка на число
     * @param str                       - строка для проверки
     */
    public static boolean isNumber(String str) {
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

}