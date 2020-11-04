![Java CI with Maven](https://github.com/Sarahcvd/http-client-demo/workflows/Java%20CI%20with%20Maven/badge.svg)

Dette prosjektet består av en HTTP-server og klient som kobles til en postgresql database. Vi har lagd tester for å se om planlagt funksjonalitet kjøres korrekt (tester mot: server, klient, querystring og database). Hovedklassen i prosjektet er server-klassen, denne kjøres for å starte servern (Forbindelse åpnes på port 8080 - via localhost). Klient-klassen brukes for å "snakke" med servern, data kan lastes opp og ned til serveren (html/txt objekter og css funksjonalitet). Det går ann å kommunisere med server i nettleseren. Workerdaoklassen lagrer bruker-input fra nettleser till databasen.

* Vi har lagd threads i en uendelig "while-loop" for å holde serveren "åpen" sånn at den alltid er tilgjengelig for nye forbindelser. Kode for å stenge forbindensen er også lagd inn, "connection-close" for at nettsiden ikke står stille når brukeren prøver å kalle på serveren.

* For å kommunisere med serveren: Kjør mvn clean for å rense /target, kjør deretter mvn package for å opprette .jar filen som du skal kjøre. Konfigurasjonsfilen din skal du døpe til "pgr203.properties", den skal inneholde:

* Verdier for dataSource.url, dataSoure.username og dataSource.password som peker på en tom database
* Etter å ha kjørt mvn package kan du kjøre serveren gjennom å skrive: java -jar filepath til jar-fil/http-client-demo-1.0-SNAPSHOT.jar

* Når brukeren har opprettet forbindelse mot serveren så har denne mulighet til å lagre informasjon til arbeidere som outputtes i en HTML fil og lagres i en database.

* Erfaringer med arbeid og løsning:
I denne oppgaven har vi vært svært gode til å jobbe sammen. Vi har vært fysisk tilstede hver gang vi har jobbet og dette har gjort at begge har fått en god forståelse av alt vi har gjort, samt at debuggingen har vært mer effektiv da begge vet hva som har blitt kodet. 

Vår erfaring med å løse denne oppgaven er at vi i større grad forstår hvordan server og client snakker sammen samt hvordan en server snakker med en database. Dette er ikke noe vi har sett på i tidligere fag. 

Vi mener dette har vært en utfordende oppgave å jobbe med, men vi har vært flinke til å bruke god tid på å forsøk å forstå all koden. Dette føler vi også at vi har gjort, men det vanskeligste er å se sammenhengen mellom alle klassene. Alt i alt synes vi at vi har klart å løse det på en bra måte og vi er fornøyde med resultatet. Vi har fulgt godt med på alle forelesninger og føler vi sitter igjen med en mye større forståelse av Java.

* Link til tilbakemeldinger til annet team: https://github.com/kristiania/pgr203eksamen-thensrud/issues


* Server structure: 
![Server Structure](docs/server_structure.png)


Prosjektet er bygget av Sarah Christine van Dijk og Wali Gustav Björk
