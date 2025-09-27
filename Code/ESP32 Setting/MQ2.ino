#include <Wire.h>
#include <LiquidCrystal_I2C.h>
#include <MQUnifiedsensor.h>
#include <Adafruit_Sensor.h>
#include <DHT.h>
#include <DHT_U.h>

// Cấu hình cảm biến và phần cứng
#define DHTPIN 15
#define DHTTYPE DHT22

#define Board "ESP-32"
#define MQ2Pin 4
#define Type "MQ-2"
#define Voltage_Resolution 3.3
#define ADC_Bit_Resolution 12
#define RatioMQ2CleanAir 9.83

#define BUZZER_PIN 2  // Chân loa (thay cho LED)

// Khởi tạo đối tượng cảm biến và LCD
MQUnifiedsensor MQ2(Board, Voltage_Resolution, ADC_Bit_Resolution, MQ2Pin, Type);
LiquidCrystal_I2C lcd(0x27, 16, 2);
DHT dht(DHTPIN, DHTTYPE);

// Biến toàn cục và semaphore
SemaphoreHandle_t xSemaphore;
float gasValue = 0;
float tempValue = 0;

void setup() {
  Serial.begin(9600);
  lcd.init();
  lcd.backlight();
  lcd.setCursor(0, 0);
  lcd.print("RTOS-Group7");
  delay(2000);
  lcd.clear();

  pinMode(BUZZER_PIN, OUTPUT);  
  digitalWrite(BUZZER_PIN, LOW); 

  dht.begin();

  MQ2.setRegressionMethod(1);
  MQ2.setA(574.25); MQ2.setB(-2.222);
  MQ2.init();
  float calcR0 = 0;
  for (int i = 1; i <= 10; i++) {
    MQ2.update();
    calcR0 += MQ2.calibrate(RatioMQ2CleanAir);
  }
  MQ2.setR0(calcR0 / 10);

  xSemaphore = xSemaphoreCreateMutex();

  xTaskCreatePinnedToCore(readGasTask, "GasTask", 4096, NULL, 1, NULL, 1);
  xTaskCreatePinnedToCore(readTempTask, "TempTask", 4096, NULL, 1, NULL, 1);
  xTaskCreatePinnedToCore(updateLcdTask, "LcdTask", 4096, NULL, 1, NULL, 1);
}

void loop() {
  // Không dùng loop vì dùng FreeRTOS
}

// Task đọc MQ2
void readGasTask(void *pvParameters) {
  while (1) {
    MQ2.update();
    gasValue = analogRead(MQ2Pin);
    gasValue = map(gasValue, 0, 4095, 0, 100);
    Serial.print("Gas: "); Serial.println(gasValue);
    vTaskDelay(200 / portTICK_PERIOD_MS);
  }
}

// Task đọc DHT22
void readTempTask(void *pvParameters) {
  while (1) {
    tempValue = dht.readTemperature();
    Serial.print("Temp: "); Serial.println(tempValue);
    vTaskDelay(200 / portTICK_PERIOD_MS);
  }
}

// Task cập nhật LCD và bật loa khi cảnh báo
void updateLcdTask(void *pvParameters) {
  while (1) {
    bool alert = (gasValue > 50 || tempValue > 70);

    if (xSemaphoreTake(xSemaphore, (TickType_t)10) == pdTRUE) {
      lcd.clear();

      if (alert) {
        lcd.setCursor(0, 0);
        lcd.print("!!! CANH BAO !!!");
        lcd.setCursor(0, 1);
        if (gasValue > 50) lcd.print("Gas Cao ");
        if (tempValue > 70) lcd.print("Nhiet Cao");

        // Bật còi cảnh báo: 3 tiếng bíp ngắn, ngắt quãng
        for (int i = 0; i < 3; i++) {
          digitalWrite(BUZZER_PIN, HIGH);
          vTaskDelay(300 / portTICK_PERIOD_MS);
          digitalWrite(BUZZER_PIN, LOW);
          vTaskDelay(300 / portTICK_PERIOD_MS);
        }

      } else {
        lcd.setCursor(0, 0);
        lcd.print("Gas:");
        lcd.print(gasValue);
        lcd.print("%");
        lcd.setCursor(0, 1);
        lcd.print("Temp:");
        lcd.print(tempValue);
        lcd.print("C");

        digitalWrite(BUZZER_PIN, LOW); // Tắt buzzer nếu không cảnh báo
      }

      xSemaphoreGive(xSemaphore);
    }

    vTaskDelay(500 / portTICK_PERIOD_MS);
  }
}
