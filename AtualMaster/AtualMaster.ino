/**
  Introdução ao uso do Modbus RTU com Opta™
  Nome: Opta_Cliente
  Objetivo: Escreve valores em Coils e Holding Registers; Lê valores de Coils, Entradas Discretas, Holding Registers e Input Registers.

  @autor Arduino
*/
#include <ArduinoHttpClient.h>
#include <ArduinoModbus.h>
#include <ArduinoRS485.h>  // A biblioteca ArduinoModbus depende da ArduinoRS485
#include <Ethernet.h>
#include <PubSubClient.h>
#include <ArduinoJson.h>



byte mac[] = { 0xA8, 0x61, 0x0A, 0x50, 0x4C, 0x7F };  //Endereço MAC  DA PLACA DE ETHERNET DO OPTA
char serverAddress[] = "10.110.2.112";                 // VARIAVEL PARA ENDEREÇO DE REDE DO SERVER, DEIXAR VAZIO SE FOR ATRIBUIR DHCP
int port = 9095;                                      //PORTA PARACOMUNICAÇÃO NODE-RED

EthernetClient ethClient;                                        //Cuida da conexão física (TCP).
HttpClient client = HttpClient(ethClient, serverAddress, port);  // Usa o EthernetClient para fazer comunicações HTTP.

EthernetServer server = EthernetServer(80);
String dadosSensores[3];
bool estadoBobina;

uint16_t ts = 0;  //TEMP.SAIDA DE AR;
uint16_t ta = 0;  //TEMP.AMBIENTE;
uint16_t to = 0;  //TEMP.OLEO;
uint16_t po = 0;  //TEMP.PONTO ORVALHO
uint16_t ps = 0;  //PRESSAO DE AR
uint16_t hc = 0;  //horimetro - horas em carga
uint16_t ht = 0;  //horimetro - horas totais
//variavel de controle para receber envio da API
bool checkRemoto = false;
//variavel de validação da mudança de estado do compressor
bool estadoEnvio = false;

//variaveis para controle de leitura dos dados sem travamento 
//esta guarda o timer da ultima leitura
unsigned long lastRead = 0;
//esta define o tempo de cada leitura;
const unsigned long READ_INTERVAL = 1000; // 1 segundo


constexpr auto baudrate{ 9600 };

// Calcula os atrasos preDelay e postDelay em microssegundos conforme a especificação do Modbus RTU
// Guia de especificação e implementação do MODBUS sobre linha serial V1.02
// Parágrafo 2.5.1.1 - Enquadramento de mensagem RTU do MODBUS
// https://modbus.org/docs/Modbus_over_serial_line_V1_02.pdf
constexpr auto bitduration{ 1.f / baudrate };
constexpr auto preDelayBR{ bitduration * 9.6f * 3.5f * 1e6 };
constexpr auto postDelayBR{ bitduration * 9.6f * 3.5f * 1e6 };
//constexpr auto preDelayBR { bitduration * 10.0f * 3.5f * 1e6 };


bool bobina = false;

void setup() {

  Serial.begin(115200);
  ModbusRTUClient.setTimeout(2000);
  Serial.begin(9600);
  while (!Serial)
    ;


  //SETUP PARA COMUNICAÇÃO COM ETHERNET VIA CABO,
  Serial.println("Inicializando Ethernet via DHCP...");

  //Inicializa Ethernet com DHCP
  if (Ethernet.begin(mac) == 0) {
    Serial.println("Falha ao configurar Ethernet via DHCP");
    while (true)
      ;  // trava o programa se DHCP falhar
  }
  
  server.begin();
  delay(1000);

  Serial.print("IP Local via DHCP: ");
  Serial.println(Ethernet.localIP());

  Serial.println("Cliente Modbus RTU");

  RS485.setDelays(preDelayBR, postDelayBR);  // TEMPO PARA TRANMISSÃO E RECEPÇÃO DOS DADOS

  // Inicia o cliente Modbus RTU
  if (!ModbusRTUClient.begin(baudrate, SERIAL_8N1)) {
    Serial.println("Falha ao iniciar o Cliente Modbus RTU!");
    while (1)
      ;
  }
}

void loop() {

  Serial.println("__________________________________________________________________");
  //retorna o tempo (em milissegundos) que o CLP fooi ligado
  unsigned long currentMillis = millis();

   if (currentMillis - lastRead >= READ_INTERVAL) {
    lastRead = currentMillis;
    


    if (readHoldingRegisterValues()) {
      enviarDadosParaAPI();
    } else {
      Serial.println("Falha na leitura Modbus");
    }
  }

  // //Feita a chamada da leitura dos registradores se caso  true ;
  // //Entra no loob enquanto for true continua lendo;
  // readHoldingRegisterValues();
  // while( readHoldingRegisterValues()){
  //   //chama a função de envio para o end point*
  //    enviarDadosParaAPI();
  
  //    delay(1000); 
  // } do {
  //   Serial.println("Falha na leitura e envio dos dados;");
  // }
  
}

bool readHoldingRegisterValues() {


  Serial.println("Lendo dados dos registradores:");


  if (ModbusRTUClient.requestFrom(167, HOLDING_REGISTERS, 0x02, 7)) {

    ts = ModbusRTUClient.read();
    to = ModbusRTUClient.read();
    po = ModbusRTUClient.read();
    ps = ModbusRTUClient.read();
    hc = ModbusRTUClient.read();
    ht = ModbusRTUClient.read();

    Serial.println("Dados lidos com sucesso:");
    Serial.print("TS: "); Serial.print(ts); Serial.println(" °C");
    Serial.print("TA: "); Serial.print(ta); Serial.println(" °C");
    Serial.print("TO: "); Serial.print(to); Serial.println(" °C");
    Serial.print("PO: "); Serial.print(po); Serial.println(" °C");
    Serial.print("PS: "); Serial.print(ps / 10.0, 1); Serial.println(" bar");
    Serial.print("HC: "); Serial.print(hc); Serial.println(" horas");
    Serial.print("HT: "); Serial.print(ht); Serial.println(" horas");
    
    return true;
  } else {
    Serial.print("Erro na leitura dos registradores: ");
    Serial.println(ModbusRTUClient.lastError());
    return false;
  }

  // Monta e retorna uma string com os dados separados por vírgula
  Serial.print("--------------------------------------------------------");
}

void ligaDesligaRemoto() {

  uint16_t valor = 0;
  checkRemoto = false;

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

    if (bitRead(envio, 1) == 1) {
      estadoBobina = true;
    } else {
      estadoBobina = false;
    }

    if (ModbusRTUClient.holdingRegisterWrite(167, 0x00, envio)) {
      Serial.println("Compressor ligado/desligado remotamente!");
    } else {
      Serial.print("Erro ao escrever: ");
      Serial.println(ModbusRTUClient.lastError());
    }

  } else {
    Serial.print("Erro ao ler: ");
    Serial.println(ModbusRTUClient.lastError());
    return;
  }
}

//receber ordem da Api para liga e desliga 
void recebeOrdemRemota(){

  client.begin();
  StaticJsonDocument<256> doc2;
  int httpCode = client.GET();

    if(httpCode == 200){
            String payload = client.getString();
            Serial.println("JSON recebido: "+ payload);
          
          DeserializationError error deserializeJson(doc2,payload);
          if(!error){   
            
            bool checkRemoto = doc2["comando"];

              if (checkRemoto == true) {
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
            Serial.println(httpCode);
          }

          client.end();
        } else {
          Serial.println("WiFi desconectado!");
        }


  

}


// envio de confirmação para API de mudança de estado do compressor 
void enviarConfirmacao(){
  StaticJsonDocument<256> confirm;
  confirm["estado"] = estadoEnvio;
  String jsonString;
  serializeJson(confirm, jsonString);

  client.beginRequest();
  client.post("https://10.110.0.1:8080/api/confirmacao");
  client.sendHeader("Content-Type", "application/json");
  client.sendHeader("Content-Length", jsonString.length());
  client.beginBody();
  client.print(jsonString);
  client.endRequest();
}


void enviarDadosParaAPI() {
  
  StaticJsonDocument<256> doc;
  doc["tempSaidaAr"] = ts;
  doc["tempOleo"] = to;
  doc["pontoOrvalho"] = po;
  doc["pressaoAr"] = ps;
  doc["horaCarga"] = hc;
  doc["horaTotal"] = ht;

  String jsonString;
  serializeJson(doc, jsonString);

  // Faz o POST
  client.beginRequest();
  client.post("https://10.110.18.10:8080/api/compressor");      
  client.sendHeader("Content-Type", "application/json");
  client.sendHeader("Content-Length", jsonString.length());
  client.beginBody();
  client.print(jsonString);
  client.endRequest();

  // Resposta opcional
  int statusCode = client.responseStatusCode();
  String response = client.responseBody();

  Serial.print("Status: ");
  Serial.println(statusCode);
  Serial.print("Resposta: ");
  Serial.println(response);
}

void enviarFalhas(){

  if (ModbusRTUClient.requestFrom(167, HOLDING_REGISTERS, 0x10, 1)) {

      int posicao;
      uint16_t byteFalhas = ModbusRTUClient.read();

      for (i=0; i < 16; i++){
        bool bit = bitRead(byteFalhas, i);
        posicao = i;

        if(bit){

          StaticJsonDocument<256> controleBit;
          confirm["bit"] = 
          String jsonString;
          serializeJson(confirm, jsonString);

          client.beginRequest();
          client.post("https://10.110.0.1:8080/api/confirmacao");
          client.sendHeader("Content-Type", "application/json");
          client.sendHeader("Content-Length", jsonString.length());
          client.beginBody();
          client.print(jsonString);
          client.endRequest();

      }

    }

  }
