void setup()
{
    Serial.begin(9600);
    pinMode(13, OUTPUT);
    Serial.println("Hello Pi. This is Arduino...");

}

void loop()
{
    Serial.println("Hello Pi. This is Arduino...");
    if (Serial.available())
    {
        flash(Serial.parseInt());
        Serial.flush();
    }

    delay(1000);
}

void flash(int n)
{
    Serial.print("Flash Times: ");
    Serial.println(n);

    for (int i = 0; i < n; i++)
    {
        digitalWrite(13, HIGH);
        delay(500);

        digitalWrite(13, LOW);
        delay(500);

        Serial.println(i + 1);
    }

    Serial.println("Flash Complete.");
}
