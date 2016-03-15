package com.example.user.notificationsys;

/**
 * Created by User on 2/9/2016.
 */
public class QuickstartPreferences {
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static  final String API_KEY="AIzaSyDLKg0R7piml_Woim675dQhSSGq3UwgT1g";
    public static final String MY_SHARED_PREFERENCE="NotificationSys";

    private static NullPerson nullPerson= new NullPerson();

    public static NullPerson getNullPerson() {
        return nullPerson;
    }

    public static void setNullPerson(NullPerson nullPerson) {
        QuickstartPreferences.nullPerson = nullPerson;
    }
}
