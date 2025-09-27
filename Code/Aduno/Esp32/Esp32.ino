
#include <ArduinoJson.h>
#include <LiquidCrystal_I2C.h>
#include <DHT.h>
#include <BH1750.h>
#include <time.h>

// LCD
LiquidCrystal_I2C lcd(0x27, 16, 2);

// BH1750
BH1750 lightMeter;

// DHT11
#define DHTPIN 25
#define DHTTYPE DHT11
DHT dht(DHTPIN, DHTTYPE);

// MQ2 & MQ135
#define MQ2_PIN 34
#define MQ135_PIN 35

// Ngưỡng cảnh báo (ppm)
#define GAS_PPM_THRESHOLD 1000
#define CO2_PPM_THRESHOLD 5000
#define TEMP_HIGH 35.0
#define TEMP_LOW 10.0
#define HUM_LOW 40.0
#define HUM_HIGH 80.0
#define LIGHT_LOW 50
#define LIGHT_HIGH 1000

unsigned long lastSendTime = 0;
const unsigned long checkInterval = 60000;

void setup() {
  Serial.begin(115200);
  lcd.init(); lcd.backlight();
  dht.begin();
  Wire.begin();
  lightMeter.begin();

  configTime(0, 0, "pool.ntp.org", "time.nist.gov");
  setenv("TZ", "GMT+7", 1); // Múi giờ Việt Nam
  tzset();
}

void loop() {
  float temp = dht.readTemperature();
  float hum = dht.readHumidity();
  float light = lightMeter.readLightLevel();
  int mq2 = analogRead(MQ2_PIN);
  int mq135 = analogRead(MQ135_PIN);

  // Chuyển đổi sang điện áp
  float mq2Volt = mq2 * 5 / 4095.0;
  float mq135Volt = mq135 * 5/ 4095.0;

  // Ước lượng ppm
  float gas_ppm = mq2Volt * 1000.0;   // MQ2: 1V ~ 1000 ppm
  float co2_ppm = mq135Volt * 1100.0; // MQ135: 1V ~ 1100 ppm

  Serial.printf("Temp: %.2f°C - Hum: %.2f%% - Gas: %.0f ppm - CO2: %.0f ppm - Lux: %.2f\n", temp, hum, gas_ppm, co2_ppm, light);

  String alerts[6];
  int alertCount = 0;

  if (temp > TEMP_HIGH) alerts[alertCount++] = "Nhiet do cao!";
  if (temp < TEMP_LOW) alerts[alertCount++] = "Nhiet do thap!";
  if (hum < HUM_LOW) alerts[alertCount++] = "Do am thap!";
  if (hum > HUM_HIGH) alerts[alertCount++] = "Do am cao!";
  if (gas_ppm > GAS_PPM_THRESHOLD) alerts[alertCount++] = "Khi gas cao!";
  if (co2_ppm > CO2_PPM_THRESHOLD) alerts[alertCount++] = "CO2 cao!";
  if (light < LIGHT_LOW) alerts[alertCount++] = "Anh sang thap!";
  if (light > LIGHT_HIGH) alerts[alertCount++] = "Anh sang cao!";

  if (alertCount > 0) {
    lcd.clear();
    lcd.setCursor(0, 0);
    lcd.print("CANH BAO:");
    for (int i = 0; i < alertCount; i++) {
      lcd.setCursor(0, 1);
      lcd.print(alerts[i].substring(0, 16));
      delay(500);
    }
    
  } else {
    lcd.clear();
    lcd.setCursor(0, 0);
    lcd.print("He thong on");
    lcd.setCursor(0, 1);
    lcd.print("Dang giam sat...");
  }

  delay(500);
}
