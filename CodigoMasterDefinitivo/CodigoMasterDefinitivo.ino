/**
  Introdução ao uso do Modbus RTU com Opta™
  Nome: Compressor Pneumatico SENAI- São Carlos// Projeto junto ao CLP OPTA DA FINDER 
  Objetivo: Comunicação e Controle de Compressor MetalPlan- Parafuso
*/

#include <ArduinoHttpClient.h>
#include <ArduinoModbus.h>
#include <ArduinoRS485.h>  // Biblioteca base para Modbus
#include <Ethernet.h>
#include <PubSubClient.h>
#include <ArduinoJson.h>

// -------------------- CONFIGURAÇÕES DE REDE --------------------//
//Aqui estou configurando o CLP que sera o cliente MOdbus(servidor Modbuus);
//Passo como referencia o MAC do dispositivo ;
byte mac[] = { 0xA8, 0x61, 0x0A, 0x50, 0x4C, 0x7F };
// IP fixo do OPTA
IPAddress ip(10, 110, 2, 127);
// Gateway da rede (roteador)
IPAddress gateway(10, 110, 2, 1);
IPAddress subnet(255, 255, 255, 0);  // Máscara de rede

// a variavel  char = o ip direcionado a instancia do Servidor aonde os dados eram ser postados(o POST ou GET.) ;
char serverAddress[] = "10.110.18.10";
//porta atribuida ao servidor que escutara as requisiçoes ;
int port = 9091;
//Instanciação do client Http para comunicação
EthernetClient ethClient;
//Para concluir a chamada é necessario passar os parametros (objeto Http, Ip da instancia, porta de comunnicação )
HttpClient client(ethClient, serverAddress, port);
EthernetServer server(80);

// -------------------- VARIÁVEIS GLOBAIS --------------------//

//Variaveis destinadas aos registradores do compressor, dados de pressao, temperatura entre outros;

uint16_t ts = 0;             // TEMP. SAÍDA DE AR
uint16_t ta = 0;             // TEMP. AMBIENTE
uint16_t to = 0;             // TEMP. ÓLEO
uint16_t po = 0;             // PONTO DE ORVALHO
uint16_t ps = 0;             // PRESSÃO DE AR
uint16_t hc = 0;             // HORAS EM CARGA
uint16_t ht = 0;             // HORAS TOTAIS
uint16_t pressaoAlivio = 0;  // 40101- Pressao de alivio
int16_t pressaoCarga = 0;    //40102- Pressao de Carga
float alivioBar = 0;
float cargaBar = 0;

//Variaves de Checkout e controle das logicas e envios de confirmaçoes
bool checkRemoto = false;
bool estadoEnvio = false;
bool estadoBobina = false;

//Definição de uma variavel que recebera controle de ultima leitura no controle de envio dos dados;
unsigned long lastRead = 0;

//Variavel de controle de tempo de leitura; De 1 em 1 segundos de interação;
const unsigned long READ_INTERVAL = 1000;  // Leitura a cada 1s
//BaudRate de comunicação MOdbus 19600 os dois lados precisam estar em acordo;
constexpr auto baudrate = 19200;
//controle de comunicação tempo de envio e resposta do TX e RX
constexpr auto bitduration = 1.f / baudrate;
constexpr auto preDelayBR = bitduration * 9.6f * 3.5f * 1e6;
constexpr auto postDelayBR = bitduration * 9.6f * 3.5f * 1e6;

// -------------------- PROTÓTIPOS --------------------//

//Os prototipos são  necessarios para não gerar erros no tempo de compilação,
//Para que o codigo saiba que as funçoes ja existem e que os tipos estao bem definidos;

bool readHoldingRegisterValues();
void enviarDadosParaAPI();
void recebeOrdemRemota();
void ligaRemoto();
void desligaRemoto();
void enviarConfirmacao();
void enviarFalhas();
bool readPressao();

// -------------------- SETUP --------------------//

void setup() {
  //Definição do tempo de transmissao da serial de debug
  Serial.begin(115200);
  //Esta chamada define o tempo maximo de espera para que o cliente modbus entenda que ouve falha de comunicação
  ModbusRTUClient.setTimeout(2000);

  //nessa instancia inicio o Ethernet como os parametros estipulados no  escopo Global
  Serial.println("Inicializando Ethernet com IP estático...");
  Ethernet.begin(mac, ip, gateway, gateway, subnet);  // mac, ip, dns, gateway, subnet

  server.begin();
  delay(1000);

  Serial.print("IP Local definido: ");
  Serial.println(Ethernet.localIP());

  RS485.setDelays(preDelayBR, postDelayBR);
  if (!ModbusRTUClient.begin(baudrate, SERIAL_8N1)) {
    Serial.println("Falha ao iniciar o Cliente Modbus RTU!");
    while (1)
      ;
  }

  Serial.println("Cliente Modbus RTU com sucesso !");
}

// -------------------- LOOP --------------------
void loop() {

  unsigned long currentMillis = millis();

  if (currentMillis - lastRead >= READ_INTERVAL) {
    lastRead = currentMillis;

    if (readHoldingRegisterValues()) {

      readPressao();

      ligaRemoto();

      readPressao();

      delay(100000);

      desligaRemoto();
    }

    //enviarDadosParaAPI();


  } else {
    Serial.println("Falha na leitura Modbus");
  }
}

// -------------------- FUNÇÃO:LER DADOS MODBUS --------------------//

bool readHoldingRegisterValues() {
  Serial.println("Lendo dados dos registradores:");

  if (ModbusRTUClient.requestFrom(167, HOLDING_REGISTERS, 0x02, 7)) {

    ts = ModbusRTUClient.read();
    ta = ModbusRTUClient.read();
    to = ModbusRTUClient.read();
    po = ModbusRTUClient.read();
    ps = ModbusRTUClient.read();
    hc = ModbusRTUClient.read();
    ht = ModbusRTUClient.read();

    float pressAr = ps / 10;

    Serial.println("Dados lidos com sucesso:");
    Serial.print("Temperatura de Ar-Comprimido: ");
    Serial.println(ts);
    Serial.print("Temperatura Ambiente: ");
    Serial.println(ta);
    Serial.print("Temperatura de Óleo(C°): ");
    Serial.println(to);
    Serial.print("Temperatura de Orvalho-Secador Integrado: ");
    Serial.println(po);
    Serial.print("Pessão do Ar-compreimido (decimos de Barg): ");
    Serial.println(pressAr);
    Serial.print(" Horímetro – horas em carga.: ");
    Serial.println(hc);
    Serial.print(" Horímetro – horas totais.: ");
    Serial.println(ht);

    return true;
  } else {
    Serial.print("Erro na leitura dos registradores: ");
    Serial.println(ModbusRTUClient.lastError());
    return false;
  }
}

bool readPressao() {

  Serial.println("Lendo registradores de pressão: ");

  if (ModbusRTUClient.requestFrom(167, HOLDING_REGISTERS, 100, 2)) {

    pressaoAlivio = ModbusRTUClient.read();
    pressaoCarga = ModbusRTUClient.read();

    alivioBar = pressaoAlivio / 10.0;
    cargaBar = pressaoCarga / 10.0;

    Serial.print("Pressão de Alívio: ");
    Serial.print(alivioBar);
    Serial.println(" bar");

    Serial.print("Pressão de Carga: ");
    Serial.print(cargaBar);
    Serial.println(" bar");
    return true;

  } else {
    Serial.print("Erro na leitura Modbus: ");
    Serial.println(ModbusRTUClient.lastError());

    return false;
  }
  
}

// -------------------- FUNÇÃO: ENVIO DE DADOS PARA API --------------------
void enviarDadosParaAPI() {

  StaticJsonDocument<256> doc;

  doc["dataHora"] = "2025-10-14T00:00:07.586Z";
  doc["estado"] = estadoBobina;
  doc["temperaturaArComprimido"] = ts;
  doc["temperaturaAmbiente"] = ta;
  doc["temperaturaOleo"] = to;
  doc["temperaturaOrvalho"] = po;
  doc["pressaoArComprimido"] = ps;
  doc["horaCarga"] = hc;
  doc["horaTotal"] = ht;
  doc["pressaoAlivio"] = alivioBar;
  doc["pressaoCarga"] = cargaBar;
 

  JsonObject compressor = doc.createNestedObject("compressor");
  compressor["id"] = 1;


  String jsonString;
  serializeJson(doc, jsonString);

  Serial.println("Enviando dados para API...");

  client.beginRequest();
  client.post("/compressor/dados");
  client.sendHeader("Content-Type", "application/json");
  client.sendHeader("Content-Length", jsonString.length());
  client.beginBody();
  client.print(jsonString);
  client.endRequest();

  int statusCode = client.responseStatusCode();
  String response = client.responseBody();

  Serial.print("Status: ");
  Serial.println(statusCode);
  Serial.print("Resposta: ");
  Serial.println(response);
}

// -------------------- FUNÇÃO: RECEBER ORDEM REMOTA --------------------
/*
void recebeOrdemRemota() {
  client.get("/api/ordemRemota");
  int statusCode = client.responseStatusCode();

  if (statusCode == 200) {
    String response = client.responseBody();
    Serial.println("JSON recebido: " + response);

    StaticJsonDocument<256> doc2;
    DeserializationError error = deserializeJson(doc2, response);

    if (!error) {
      bool comando = doc2["comando"];
      if (comando) {
        ligaDesligaRemoto();
        estadoEnvio = true;
      } else {
        Serial.println("Comando remoto: desligado");
      }
    } else {
      Serial.println("Erro ao desserializar JSON!");
    }
  } else {
    Serial.print("Erro HTTP: ");
    Serial.println(statusCode);
  }
}
*/

// -------------------- FUNÇÃO: LIGAR /REMOTO --------------------
void ligaRemoto() {
  uint16_t valor = 0;

  if (ModbusRTUClient.requestFrom(167, HOLDING_REGISTERS, 0x00, 1)) {
    valor = ModbusRTUClient.read();
    uint8_t byteLow = valor & 0xFF;
    uint8_t byteHigh = (valor >> 8) & 0xFF;
    uint8_t estado = bitRead(byteLow, 1);

    if (estado == 0) {
      bitSet(byteLow, 1);
    } else {
      bitClear(byteLow, 1);
    }

    uint16_t envio = (byteHigh << 8) | byteLow;
    estadoBobina = bitRead(envio, 1);

    if (ModbusRTUClient.coilWrite(167, 0x00, estadoBobina)) {
      Serial.println("Compressor ligado/desligado remotamente!");
    } else {
      Serial.print("Erro ao escrever: ");
      Serial.println(ModbusRTUClient.lastError());
    }
  } else {
    Serial.print("Erro ao ler: ");
    Serial.println(ModbusRTUClient.lastError());
  }
}

//---------------------FUNÇÃO DESLIGA CONFIRMAÇÃO----------------------------------------//

void desligaRemoto() {
  uint16_t valor = 0;

  if (ModbusRTUClient.requestFrom(167, HOLDING_REGISTERS, 0x00, 1)) {
    valor = ModbusRTUClient.read();
    uint8_t byteLow = valor & 0xFF;
    uint8_t byteHigh = (valor >> 8) & 0xFF;
    uint8_t estado = bitRead(byteLow, 1);

    if (estado == 1) {
      bitClear(byteLow, 1);
    }

    uint16_t envio = (byteHigh << 8) | byteLow;
    estadoBobina = bitRead(envio, 1);

    if (ModbusRTUClient.coilWrite(167, 0x00, estadoBobina)) {
      Serial.println("Compressor ligado/desligado remotamente!");
    } else {
      Serial.print("Erro ao escrever: ");
      Serial.println(ModbusRTUClient.lastError());
    }
  } else {
    Serial.print("Erro ao ler: ");
    Serial.println(ModbusRTUClient.lastError());
  }
}

// -------------------- FUNÇÃO: ENVIAR CONFIRMAÇÃO --------------------
void enviarConfirmacao() {
  StaticJsonDocument<256> confirm;
  confirm["estado"] = estadoEnvio;
  String jsonString;
  serializeJson(confirm, jsonString);

  client.beginRequest();
  client.post("/api/confirmacao");
  client.sendHeader("Content-Type", "application/json");
  client.sendHeader("Content-Length", jsonString.length());
  client.beginBody();
  client.print(jsonString);
  client.endRequest();

  int statusCode = client.responseStatusCode();
  Serial.print("Status confirmacao: ");
  Serial.println(statusCode);
}

// -------------------- FUNÇÃO: ENVIAR FALHAS --------------------
void enviarFalhas() {
  if (ModbusRTUClient.requestFrom(167, HOLDING_REGISTERS, 0x10, 1)) {
    uint16_t byteFalhas = ModbusRTUClient.read();

    for (int i = 0; i < 16; i++) {
      bool bit = bitRead(byteFalhas, i);

      if (bit) {
        StaticJsonDocument<256> doc;
        doc["falha"] = i;
        String jsonString;
        serializeJson(doc, jsonString);

        client.beginRequest();
        client.post("/api/falhas");
        client.sendHeader("Content-Type", "application/json");
        client.sendHeader("Content-Length", jsonString.length());
        client.beginBody();
        client.print(jsonString);
        client.endRequest();

        Serial.print("Falha enviada: bit ");
        Serial.println(i);
      }
    }
  }
}
