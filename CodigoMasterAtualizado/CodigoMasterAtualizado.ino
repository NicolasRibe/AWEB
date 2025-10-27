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
//DNS publico Explicito 
IPAddress dns(8, 8, 8, 8); 
IPAddress subnet(255, 255, 255, 0);  // Máscara de rede
//Instancia de Servidor Local do Opta, para as requesiçoes da API;
// nao sera necessario porque a API derencia tudo mas a porta esta aberta para possiveis implementaçoes 
EthernetServer server(80);

// a variavel  char = o ip direcionado a instancia do Servidor aonde os dados eram ser postados(o POST ou GET.) ;
char serverAddress[] = "10.110.18.10";
//porta atribuida ao servidor que escutara as requisiçoes ;
int port = 9091;
//Instanciação do client Http para comunicação
EthernetClient ethClient;

//Para concluir a chamada é necessario passar os parametros (objeto Http, Ip da instancia, porta de comunnicação )

  // PubSubClient mqttClient(ethClient);

  // const char* mqtt_server = "10.110.18.11";  // IP do seu broker MQTT
  // const int mqtt_port = 18083;                // Porta  MQTT


//
HttpClient client(ethClient, serverAddress, port);

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
float alivioBar = pressaoAlivio ; //parametro de alivio
float cargaBar = pressaoCarga ; //parametro de carga 
String falha = "0x00"; //variavel para envio de falhas
//------------------------------------------------------------ 
String alerta = "0x00"; // variavel para alertas


//Variaves de Checkout e controle das logicas e envios de confirmaçoes
bool checkRemoto = false;
bool estadoEnvio = false;
bool estadoBobina = false;
String estadoCompressor = "Desligado";

//Definição de uma variavel que recebera controle de ultima leitura no controle de envio dos dados;
unsigned long lastRead = 0;

//Definindo variavel para leitura de comando; 
//interação dos comandos;
unsigned long lastComando = 0;

//Variavel de controle de tempo de leitura; De 1 em 1 segundos de interação;
const unsigned long READ_INTERVAL = 300000;  // Leitura a cada 3s
//Variavel de controle de tempo de comandos;
const unsigned long INTERVALO_COMANDO = 10000; // 10 segundos
time_t agora;// variavel para adicionar hora e data atuais;


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
bool recebeOrdemRemota();
void ligaRemoto();
void desligaRemoto();
void enviarConfirmacao();
void enviarFalhas();
bool readPressao();
void envioDeEstados();
void initializer();
void realTime();

// -------------------- SETUP --------------------//
//......................................................................................................................................//
void setup() {

  //Set do relogio interno do Controlador 
  //set_time(1761557247);
  //Definição do tempo de transmissao da serial de debug
  Serial.begin(115200);

  //Esta chamada define o tempo maximo de espera para que o cliente modbus entenda que ouve falha de comunicação
  ModbusRTUClient.setTimeout(2000);

  //nessa instancia inicio o Ethernet como os parametros estipulados no  escopo Global
  Serial.println("Inicializando Ethernet com IP estático...");
  Ethernet.begin(mac, ip, dns, gateway, subnet);  // mac, ip, dns, gateway, subnet

  //SERVIÇO DE CONEXAO EXTERNA SERVIDOR OPTA 
  server.begin();
  // Passa os parametros do MQTT
  // mqttClient.setServer(mqtt_server, mqtt_port);
  //  //Chama a função callBack 
  // mqttClient.setCallback(callbackMQTT); 

  // connectMQTT();

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

  //Função para desenvolver a leitura antes de envio de dados;

  // if (ModbusRTUClient.requestFrom(167, HOLDING_REGISTERS, 0x00, 1)) {
  //   uint16_t valor1 = 0;
  //   valor1 = ModbusRTUClient.read();
  //   uint8_t byteLow = valor1 & 0xFF;
  //   uint8_t byteHigh = (valor1 >> 8) & 0xFF;
  //   uint8_t estado = bitRead(byteLow, 1);

  //   initializer(estado);

  // }
}

// -------------------- LOOP --------------------
void loop() {

  realTime();
  
  unsigned long currentMillis = millis();

  delay(1000);
 // readHoldingRegisterValues();

  //Condição de tempo de leitura dos comandos 
  if (currentMillis - lastComando >= INTERVALO_COMANDO) {
      lastComando = currentMillis;
      if (recebeOrdemRemota()) {
        enviarConfirmacao();
        enviarDadosParaAPI();
        enviarFalhas();
      }
  }
  //Condição dos tempos de intervalo entre as leituras dos registradores
  // if (currentMillis - lastRead >= READ_INTERVAL) {
  //   lastRead = currentMillis;

  //   //Se a leitura Modbus for bem sucedida
  //   // O programa le os registradores de pressao;
  //   //envia os dados para a API
  //   if (readHoldingRegisterValues()) {
  //     readPressao();
  //     enviarDadosParaAPI();
  //     enviarFalhas();

  //     enviarFalhas();
  //     delay(1000);
      
  //   }
  // }

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
// -------------------- FUNÇÃO: ENVIO DE DADOS PARA API --------------------
void enviarDadosParaAPI() {

  StaticJsonDocument<256> doc;

  doc["estado"] = estadoCompressor;
  doc["ligado"] = estadoBobina;
  doc["temperaturaArComprimido"] = ts;
  doc["temperaturaAmbiente"] = ta;
  doc["temperaturaOleo"] = to;
  doc["temperaturaOrvalho"] = po;
  doc["pressaoArComprimido"] = ps;
  doc["horaCarga"] = hc;
  doc["horaTotal"] = ht;
  doc["pressaoAlivio"] = alivioBar;
  doc["pressaoCarga"] = cargaBar;
  doc["compressorId"] = 1;  

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
  Serial.println(jsonString);
  
  int statusCode = client.responseStatusCode();
  String response = client.responseBody();

  Serial.print("Status: ");
  Serial.println(statusCode);
  Serial.print("Resposta: ");
  Serial.println(response);

  client.stop();
}
//..................... LEITURA DOS PARAMETROS DIMENCIONADOS DE PRESSAO.....................................
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

//............... funcao de receber ordem remota para mudança LIGA/DESLIGA...............................

bool recebeOrdemRemota() {
  //END POINT PARA COMUNICAÇÃO COM API
  client.get("/compressor/comando/1");
  //RECEBIDO O STATUS DE COMUNICAÇÃO 
  int statusCode = client.responseStatusCode();
  //SE STATUS OK
  if (statusCode == 200) {
    //RESPOSTA = CORPO DO JSON 
    String response = client.responseBody();
    
    //RETIRAR APOS A IMPLEMENTAÇÃO REAL 
    Serial.println("JSON recebido: " + response);

    // CRIAÇÃO DE UM OBJETOJASON PARA DESERIALIZAÇÃO COM TAMANHO DE 2 BYTES 
    StaticJsonDocument<256> doc2;
    //TRATANDO OS POSSIVEIS ERROS 
    DeserializationError error = deserializeJson(doc2, response);
    //NAO CONTENDO ERROS :ELE DESERIALIZA 
    if (!error) {
      //VARIAVEL PARA RECEBER O VALOR OU TRUE OU FALSE

      bool comando = doc2["comando"];
      //NESSE MOMENTO DEPENDENDO DO ENVIO DA API O COMPRESSOR É LIGADO OU DESLIGADO :
      //ESTA COM LOGICA PARA DUAS FUNCTIONS MAS PODEMOS MUDAR 
      if (comando) {
        ligaRemoto();
        estadoEnvio = true;
        Serial.println("Comando remoto: Ligado");
      } else if(!comando){
        desligaRemoto();
        estadoEnvio = false;
        Serial.println("Comando remoto: desligado");
      }

      client.stop();  //  fecha a conexão depois de tudo
      return true;    //leitura e execução OK

    } else {
      Serial.println("Erro ao desserializar JSON!");
      client.stop();
      return false;
    }

  } else {
    Serial.print("Erro HTTP: ");
    Serial.println(statusCode);
    client.stop();
    return false;
  }
}


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
    } 
    //comentado mas poddemos deixar tudo na mesma função 
    /*else if(estado == 1){
      bitClear(byteLow, 1);
    }*/

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
//---------------------------------------------------------------------------------------//
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

// .......................FUNÇÃO DE ENVIAR CONFIRMAÇÃO DE LIGA/DESLIGA ..............................................................
void enviarConfirmacao() {
  StaticJsonDocument<256> confirm;
  confirm["ligado"] = estadoEnvio;
  String jsonString;
  serializeJson(confirm, jsonString);

  client.beginRequest();
  client.post("/compressor/estado");
  client.sendHeader("Content-Type", "application/json");
  client.sendHeader("Content-Length", jsonString.length());
  client.beginBody();
  client.print(jsonString);
  client.endRequest();

  int statusCode = client.responseStatusCode();
  Serial.print("Status confirmacao: ");
  Serial.println(statusCode);

  client.stop();
}

//........................FUNÇAÕ DE ENVIAR FALHAS ......................................................
void enviarFalhas() {
  if (ModbusRTUClient.requestFrom(167, HOLDING_REGISTERS, 0x10, 1)) {
    uint16_t byteFalhas = ModbusRTUClient.read();

   uint8_t byteLow = byteFalhas & 0xFF;
   uint8_t byteHigh = (byteFalhas >> 8 ) & 0xFF;

    switch(byteLow){

      case 0x01: falha = "0x01"; break;
      case 0x02: falha = "0x02"; break;
      case 0x03: falha = "0x03"; break;
      case 0x04: falha = "0x04"; break;
      case 0x05: falha = "0x05"; break;
      case 0x06: falha = "0x06"; break;
      case 0x07: falha = "0x07"; break;
      case 0x08: falha = "0x08"; break;
      case 0x09: falha = "0x09"; break;
      case 0x0A: falha = "0x0A"; break;
      case 0x0B: falha = "0x0B"; break;
      case 0x0C: falha = "0x0C"; break;
      case 0x0D: falha = "0x0D"; break;
      case 0x0E: falha = "0x0E"; break;
      case 0x0F: falha = "0x0F"; break;
      case 0x10: falha = "0x10"; break;
      case 0x11: falha = "0x11"; break;
      default: falha= "0x00";break;

    }
    switch(byteHigh){
      case 0x12: alerta = "0x12"; break;
      case 0x13: alerta = "0x13"; break;
      case 0x14: alerta = "0x14"; break;
      case 0x15: alerta = "0x15"; break;
      case 0x16: alerta = "0x16"; break;
      case 0x17: alerta = "0x17"; break;
      case 0x18: alerta = "0x18"; break;
      case 0x19: alerta = "0x19"; break;
      case 0x1A: alerta = "0x1A"; break;
      default: alerta= "0x00";
    }

    StaticJsonDocument<256> doc;
    doc["falha"] = falha;
    doc["alerta"]= alerta; //Montando o Json com duas chaves e valores distintos;
    String jsonString;//Serializar o json em uma string
    serializeJson(doc, jsonString);
    client.beginRequest();
    client.post("/api/falhas"); //enviando via Http post
    client.sendHeader("Content-Type", "application/json");
    client.sendHeader("Content-Length", jsonString.length());
    client.beginBody();
    client.print(jsonString);
    client.endRequest();

    int statusCode = client.responseStatusCode();// Lê a resposta do servidor 
    String response = client.responseBody();

    client.stop();

    Serial.print("Falha enviada:  ");
    Serial.println(falha);
    Serial.println(alerta);
      
  }
}
//................................FUNCAO DE  ESTADOS DE FUNCIONAMENTO .............................................
void envioDeEstados(){

  if (ModbusRTUClient.requestFrom(167, HOLDING_REGISTERS, 0x09, 1)){
    uint16_t statusCompressor = ModbusRTUClient.read();

    uint8_t byteLow = statusCompressor & 0xFF;


    switch(byteLow){
      case 0x00:
      case 0x01:
      case 0x02:
        estadoCompressor = "Desligado";
        break;

      case 0x0A:
      case 0x0B:
      case 0x0C:
      case 0x0D:
      case 0x0E:
      case 0x0F:
      case 0x10:
      case 0x11:
        estadoCompressor = "PARTINDO";
        break;

      case 0x14:
      case 0x15:
        estadoCompressor = "ALIVIO";
        break;
      
      case 0x1E:
        estadoCompressor = "EMCARGA";
        break;

      case 0x28:
        estadoCompressor = "STANDBY";
        break;

      case 0x32:
        estadoCompressor = "PARANDO";
        break;

      default:
        estadoCompressor = "DESCONHECIDO";
        break;


    }

    Serial.println("Status do Compressor: " + estadoCompressor);
  }

  StaticJsonDocument<256> Compress; 
  Compress["compressorId"] = 1;   
  Compress["ligado"] = estadoCompressor;

  String jsonString;
  serializeJson(Compress, jsonString);
  

  client.beginRequest();
  client.post("/compressor/estado");
  client.sendHeader("Content-Type", "application/json");
  client.sendHeader("Content-Length", jsonString.length());
  client.beginBody();
  client.print(jsonString);
  client.endRequest();

  
  int statusCode = client.responseStatusCode();
  String response = client.responseBody();
  Serial.print("Status confirmacao: ");
  Serial.println(statusCode);
  Serial.print("Resposta estados: ");
  Serial.println(response);

  client.stop();
  

}
//.....................FUNÇÃO PARA INICIAR SIISTEMA E MANDAR ESTADO DE FUNCIONAMENTO.....................................................

void initializer(bool estadoAtual){

  StaticJsonDocument<256> confirm;
  confirm["compressorId"] = 1;
  confirm["ligado"] = estadoAtual;
  String jsonString;
  serializeJson(confirm, jsonString);

  client.beginRequest();
  client.post("/compressor/estado");
  client.sendHeader("Content-Type", "application/json");
  client.sendHeader("Content-Length", jsonString.length());
  client.beginBody();
  client.print(jsonString);
  client.endRequest();
  Serial.println(jsonString);

  int statusCode = client.responseStatusCode();
  Serial.print("Status confirmacao: ");
  Serial.println(statusCode);

  client.stop();

}

//...........................FUNÇÃO LEITURA DE TEMPO E CONTROLE DE LIGA/DESLIGA POR HORARIO ....................................................
 void realTime(){

    time_t t= time(NULL);
    Serial.println(t);
    //Ajuste para o horario de Brasilia (-3h)
    agora = t - 3 * 3600;
    Serial.print("Data e hora atuais:");
    Serial.print(ctime(&agora));
    // quebra em partes /hora /dia // mes
    struct tm *info = localtime(&agora);

    int hora = info->tm_hour;//hora atual
    int minuto =info->tm_min;//minuto atual.
    int segundo = info->tm_sec;//segundo atual

    if (hora == 6 && minuto == 30 && estadoBobina == 0x00  ){
      ligaRemoto();
      enviarConfirmacao();

    }else if (hora == 23 && estadoBobina == 0x01 ){
      desligaRemoto();
      enviarConfirmacao();
    }

    
  }
