package com.example.T6;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.json.JSONObject;
//import net.minidev.json.JSONObject;

@CrossOrigin
@Controller
public class MyController {
    private final RestTemplate restTemplate;

    @Autowired
    public MyController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/")
    public String indexPage() {
        return "index";
    }

    @GetMapping("/receiveClassUnderTest")
    public ResponseEntity<String> receiveClassUnderTest(
            @RequestParam("idUtente") String idUtente,
            @RequestParam("idPartita") String idPartita,
            @RequestParam("idTurno") String idTurno,
            @RequestParam("nomeCUT") String nomeCUT,
            @RequestParam("robotScelto") String robotScelto,
            @RequestParam("difficolta") String difficolta) {
        try {
            // Esegui la richiesta HTTP al servizio esterno per ottenere il file
            // ClassUnderTest.java
            String url = "http://manvsclass-controller-1:8080/downloadFile/" + nomeCUT;
            String classUnderTest = restTemplate.getForObject(url, String.class);

            JSONObject resp = new JSONObject();
            resp.put("class", classUnderTest);

            // Restituisci una risposta di successo
            return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
        } catch (Exception e) {
            System.err.println(e);
            // Gestisci eventuali errori e restituisci una risposta di errore
            return new ResponseEntity<>("Errore durante la ricezione del file ClassUnderTest.java",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private final HttpClient httpClient = HttpClientBuilder.create().build();

    // questa è la parte in cui interagiamo con T7
    @PostMapping("/sendInfo") // COMPILA IL CODICE DELL'UTENTE E RESTITUISCE OUTPUT DI COMPILAZIONE CON MVN
    public void handleSendInfoRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            // Leggi il contenuto dei file inviati
            String testingClassName = request.getParameter("testingClassName");
            String testingClassCode = request.getParameter("testingClassCode");
            String underTestClassName = request.getParameter("underTestClassName");
            String underTestClassCode = request.getParameter("underTestClassCode");

            // Esegui le operazioni necessarie con i dati ricevuti

            // Esegui la richiesta HTTP al servizio di destinazione
            HttpPost httpPost = new HttpPost("URL_DEL_SERVIZIO_COMPILEANDCOVERAGE");

            // Aggiungi i dati dei file alla richiesta
            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create()
                    .addTextBody("testingClassName", testingClassName)
                    .addTextBody("testingClassCode", testingClassCode)
                    .addTextBody("underTestClassName", underTestClassName)
                    .addTextBody("underTestClassCode", underTestClassCode)
                    .addBinaryBody("file1", getFileBytes("path/to/file1.txt"))
                    .addBinaryBody("file2", getFileBytes("path/to/file2.txt"));

            HttpEntity entity = (HttpEntity) entityBuilder.build();
            httpPost.setEntity(entity);

            // Esegui la richiesta e ottieni la risposta
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity responseEntity = httpResponse.getEntity();

            // Leggi la risposta come stringa
            String responseBody = EntityUtils.toString(responseEntity);

            // Imposta l'intestazione della risposta
            response.setStatus(HttpStatus.OK.value());
            response.setContentType(MediaType.TEXT_PLAIN_VALUE);
            response.setCharacterEncoding("UTF-8");

            // Scrivi la risposta nel corpo della risposta
            response.getWriter().write(responseBody);
        } catch (IOException e) {
            // Gestisci l'eccezione o restituisci un errore appropriato
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    private byte[] getFileBytes(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readAllBytes(path);
    }

    @GetMapping("/getResultXml") // COMPILA IL CODICE DELL'UTENTE E RITORNA IL FILE XML DI JACOCO CON LA COVERAGE
    public String handleGetResultXmlRequest() {
        try {
            // Esegui la richiesta HTTP al servizio di destinazione
            HttpGet httpGet = new HttpGet("URL_DEL_SERVIZIO_DESTINAZIONE");

            HttpResponse targetServiceResponse = httpClient.execute(httpGet);

            // Verifica lo stato della risposta
            int statusCode = targetServiceResponse.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.OK.value()) {
                // Leggi il contenuto del file XML dalla risposta
                HttpEntity entity = targetServiceResponse.getEntity();
                String xmlContent = EntityUtils.toString(entity);

                // Restituisci il contenuto del file XML come risposta al client
                return xmlContent;
            } else {
                // Restituisci un messaggio di errore al client
                return "Errore durante il recupero del file XML.";
            }
        } catch (Exception e) {
            // Gestisci eventuali errori e restituisci un messaggio di errore al client
            return "Si è verificato un errore durante la richiesta del file XML.";
        }
    }

    // FUNZIONE CHE DOVREBBE RICEVERE I RISULTATI DEI ROBOT
    @GetMapping("/getResultRobot") // NON ESISTE NESSUN INTERFACCIA VERSO I COMPILATORI DEI ROBOT EVOSUITE E
                                   // RANDOOP
    public String handleGetResultRobotRequest() {
        try {
            // Esegui la richiesta HTTP al servizio di destinazione
            HttpGet httpGet = new HttpGet("URL_DEL_SERVIZIO_DESTINAZIONE");

            HttpResponse targetServiceResponse = httpClient.execute(httpGet);

            // Verifica lo stato della risposta
            int statusCode = targetServiceResponse.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.OK.value()) {
                // Leggi il contenuto del file XML dalla risposta
                HttpEntity entity = targetServiceResponse.getEntity();
                String xmlContent = EntityUtils.toString(entity);

                // Restituisci il contenuto del file XML come risposta al client
                return xmlContent;
            } else {
                // Restituisci un messaggio di errore al client
                return "Errore durante il recupero del file XML.";
            }
        } catch (Exception e) {
            // Gestisci eventuali errori e restituisci un messaggio di errore al client
            return "Si è verificato un errore durante la richiesta del file XML.";
        }
    }

    @GetMapping("/getJaCoCoReport")
    public ResponseEntity<String> getJaCoCoReport() {
        try {
            HttpGet httpGet = new HttpGet("http://localhost:1234/compile-and-codecoverage");

            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();

            String responseBody = EntityUtils.toString(entity);
            JSONObject responseObj = new JSONObject(responseBody);

            String xml_string = responseObj.getString("coverage");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_XML);
            // headers.setContentDisposition(ContentDisposition.attachment().filename("index.html").build());

            return new ResponseEntity<>(xml_string, headers, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/inviaDatiEFile")
    public ResponseEntity<String> handleInviaDatiEFileRequest(
            @RequestParam("idUtente") String idUtente,
            @RequestParam("idPartita") String idPartita,
            @RequestParam("idTurno") String idTurno,
            @RequestParam("robotScelto") String robotScelto,
            @RequestParam("difficolta") String difficolta,
            @RequestParam("file") MultipartFile file,
            @RequestParam("playerTestClass") String playerTestClass) {
        try {
            // Creazione di una richiesta HTTP POST al servizio di destinazione
            HttpPost httpPost = new HttpPost("URL_DEL_SERVIZIO_DESTINAZIONE");// CHIAMA UPDATE TURN TASK4
            // Creazione del corpo della richiesta multipart
            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create()
                    .addTextBody("idUtente", idUtente)
                    .addTextBody("idPartita", idPartita)
                    .addTextBody("idTurno", idTurno)
                    .addTextBody("robotScelto", robotScelto)
                    .addTextBody("difficolta", difficolta)
                    .addTextBody("playerTestClass", playerTestClass) // Aggiungi la classe Java come parte del corpo
                                                                     // della richiesta
                    .addBinaryBody("file", file.getBytes(), ContentType.APPLICATION_OCTET_STREAM,
                            file.getOriginalFilename());
            httpPost.setEntity((HttpEntity) entityBuilder.build());
            // Esecuzione della richiesta HTTP al servizio di destinazione
            HttpResponse targetServiceResponse = httpClient.execute(httpPost);
            // Restituisci una risposta di successo
            return ResponseEntity.ok("Dati, file e classe Java inviati con successo");
        } catch (Exception e) {
            // Gestisci eventuali errori e restituisci una risposta di errore
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore durante l'invio dei dati, del file e della classe Java");
        }
    }
}