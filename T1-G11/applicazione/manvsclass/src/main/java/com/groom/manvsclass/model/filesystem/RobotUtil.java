package com.groom.manvsclass.model.filesystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputFilter.Config;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.web.multipart.MultipartFile;

public class RobotUtil {

    public static void generateAndSaveRobots(String fileName, String cname, MultipartFile multipartFile) throws IOException {
        // RANDOOP - T9			    
		Path directory = Paths.get("/VolumeT9/app/FolderTree/" + cname + "/" + cname + "SourceCode");
		
		try {
			// Verifica se la directory esiste già
			if (!Files.exists(directory)) {
				// Crea la directory
				Files.createDirectories(directory);
				System.out.println("La directory è stata creata con successo.");
			} else {
				System.out.println("La directory esiste già.");
			}
		} catch (Exception e) {
			System.out.println("Errore durante la creazione della directory: " + e.getMessage());
		}

		try (InputStream inputStream = multipartFile.getInputStream()) {
			Path filePath = directory.resolve(fileName);
			System.out.println(filePath.toString());
			Files.copy(inputStream,filePath,StandardCopyOption.REPLACE_EXISTING);
		}

        ProcessBuilder processBuilder = new ProcessBuilder();

        processBuilder.command("/usr/lib/jvm/java-1.8-openjdk/bin/java", "-jar", "Task9-G19-0.0.1-SNAPSHOT.jar");
        processBuilder.directory(new File("/VolumeT9/app/"));
    
        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null)
            System.out.println(line);
			
        reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        while ((line = reader.readLine()) != null)
            System.out.println(line);

        try {
			int exitCode = process.waitFor();

			System.out.println("ERRORE CODE: " + exitCode);
		} catch (InterruptedException e) {
			System.out.println(e);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        // EVOSUITE - T8
		// TODO: RICHIEDE AGGIUSTAMENTI IN T8
    }
    

}
