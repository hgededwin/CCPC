package com.cacaopaycard.cacaopay.mvp.util;

public class URLCacao {


    private final static String HOST_NAME = "http://167.250.76.148:8888/";
    private final static String API = "api/cacao/";

    public final static String URL_USER_DATA = HOST_NAME + API + "user_data"; // sin implementar
    public final static String URL_CARD_BALANCE = HOST_NAME + API + "card_balance"; // sin implementar
    public final static String URL_CARD_MOVEMENTS = HOST_NAME + API + "tarjeta/consultar/movimientos";
    public final static String URL_LOCK_CARD =  HOST_NAME + API + "tarjeta/bloquear";

    // nuevos servicios
    public final static String URL_CREAR_CUENTA = HOST_NAME + API + "cuenta/crear";
    public final static String URL_LOGIN = HOST_NAME + API + "cuenta/login";
    public final static String URL_RECUPERAR_PASSWORD = HOST_NAME + API + "cuenta/recuperar/clave";


    //private final static String HOST_NAME = "https://cocoa.cacaopaycard.com/";
    //private final static String HOST_NAME = "https://cocoa-staging.cacaopaycard.com/";
    //private final static String API = "api/v1/";

    /*public final static String URL_USER_DATA = HOST_NAME + API + "user_data";
    public final static String URL_CARD_BALANCE = HOST_NAME + API + "card_balance";
    public final static String URL_CARD_MOVEMENTS = HOST_NAME + API + "moves";
    public final static String URL_LOCK_CARD =  HOST_NAME + API + "locks_blocks_cards";*/

    /*
    <string name="url_login">https://cocoa.cacaopaycard.com/api/v1/login_user</string>
    <string name="url_update_password">https://cocoa.cacaopaycard.com/api/v1/update_password</string>
    <string name="url_reenvio_pin_registro">https://cocoa.cacaopaycard.com/api/v1/pin_forwarding</string>
    <string name="url_forgot_pass">https://cocoa.cacaopaycard.com/api/v1/forgot_password</string>
    <string name="url_registro">https://cocoa.cacaopaycard.com/api/v1/register_user</string>
    <string name="url_add_card">https://cocoa.cacaopaycard.com/api/v1/add_card</string>
    <string name="url_delete_card">https://cocoa.cacaopaycard.com/api/v1/delete_card</string>
    <string name="url_change_nip">https://cocoa.cacaopaycard.com/api/v1/change_nip</string>
    <string name="url_tranfer_terceros">https://cocoa.cacaopaycard.com/api/v1/transfer_terceros</string>
    <string name="url_tranfer_send">https://cocoa.cacaopaycard.com/api/v1/transfer_send</string>
    <string name="url_init_transfer">https://cocoa.cacaopaycard.com/api/v1/transfer</string>
    <string name="url_lock_unlock">https://cocoa.cacaopaycard.com/api/v1/locks_blocks_cards</string>

     */
}
