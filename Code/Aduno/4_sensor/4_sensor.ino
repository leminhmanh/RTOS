#include <Wire.h>
#include <LiquidCrystal_I2C.h>
#include <BH1750.h>
#include <Adafruit_Sensor.h>
#include <DHT.h>
#include <DHT_U.h>

// LCD và BH1750
LiquidCrystal_I2C lcd(0x27, 16, 2);
BH1750 lightMeter;

// DHT11
#define DHTPIN 33
#define DHTTYPE DHT11
DHT dht(DHTPIN, DHTTYPE);

// MQ analog
#define MQ2_PIN   35
#define MQ135_PIN 32

void setup() {
  Serial.begin(115200);
  lcd.init();
  lcd.backlight();
  lcd.setCursor(0, 0);
  lcd.print("  Khoi dong...");

  Wire.begin();
  lightMeter.begin();
  dht.begin();

  delay(2000);
  lcd.clear();
}

void loop() {
  // Đọc DHT11
  float tempC = dht.readTemperature();
  float humidity = dht.readHumidity();

  // Đọc analog MQ2 + MQ135
  int mq2 = analogRead(MQ2_PIN);
  int mq135 = analogRead(MQ135_PIN);

  // Đọc ánh sáng từ BH1750
  float lux = lightMeter.readLightLevel();

  // Dòng 1: Hiển thị nhiệt độ & lux
  lcd.setCursor(0, 0);
  lcd.print("T:");
  if (isnan(tempC)) lcd.print("--");
  else lcd.print((int)round(tempC));
  lcd.print((char)223); // độ C
  lcd.print(" ");

  lcd.print("L:");
  if (lux < 0) lcd.print("--");
  else lcd.print((int)lux);
  lcd.print("lx");

  // Dòng 2: Cảnh báo nếu vượt ngưỡng
  lcd.setCursor(0, 1);
  if (isnan(tempC)) {
    lcd.print("Loi cam bien DHT ");
  } else if (tempC > 40) {
    lcd.print("Canh bao: Nhiet do");
  } else if (mq2 > 800) {
    lcd.print("Canh bao: Khi gas! ");
  } else if (mq135 > 800) {
    lcd.print("Canh bao: CO2/NH3! ");
  } else if (lux < 10) {
    lcd.print("Canh bao: Toi qua  ");
  } else {
    lcd.print("Tat ca OK         ");
  }

  // Debug Serial
  Serial.print("Temp: "); Serial.print(tempC); Serial.print(" °C | ");
  Serial.print("Hum: "); Serial.print(humidity); Serial.print(" % | ");
  Serial.print("MQ2: "); Serial.print(mq2); Serial.print(" | ");
  Serial.print("MQ135: "); Serial.print(mq135); Serial.print(" | ");
  Serial.print("Lux: "); Serial.println(lux);

  delay(1500);
}
