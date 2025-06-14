# A Simple Firewall in Java

<u>**A Simple Firewall in Java (ASFJ)**</u> è un progetto sviluppato per la prova finale del corso di "Tecniche Avanzata di Programmazione", edizione 2024-2025, Università degli Studi della Basilicata, laurea magistrale in Ingegneria informatica e delle tecnologie dell'informazione.

### Introduzione

### Architettura del progetto

Il progetto è stato strutturato in modo modulare per favorire la manutenibilità, la testabilità e la riusabilità del codice. Sono stati definiti quattro sottoprogetti principali, organizzati secondo i principi di sviluppo delle moderne architettura software come: separazione delle responsabilità, inversione del controllo e isolamento dei componenti:

1) **firewall-api**: contiene le interfacce condivise tra i moduli, come IFirewall, IRuleSet, IRule, IPacket ecc. Questo modulo rappresenta il contratto che lega insieme gli altri componenti e consente un disaccoppiamento chiaro tra implementazioni e dipendenze.

2) **firewall-core**: implementa la logica centrale del firewall, compresi il controllo dei pacchetti, la gestione dei set di regole(caricamento da file, test su varianti del set), la gestione delle regole di filtraggio(aggiunta, modifica e rimozione). È progettato per essere indipendente dall'interfaccia utente, rendendolo facilmente testabile e riutilizzabile in diversi contesti (es. interfacce grafiche alternative, servizi web, test automatici).

3) **firewall-gui**: fornisce l'interfaccia grafica Desktop Swing per l’interazione con il firewall. Si appoggia unicamente alle interfacce definite in firewall-api per interagire con la logica sottostante, ed è conforme con l’architettura Model-View-Controller(MVC). La GUI consente la visualizzazione dei pacchetti bloccati, la modifica “live” delle regole e la gestione interattiva del set di regole.

4) **client-simulator**: simula il comportamento di client multipli che inviano pacchetti concorrenti verso il firewall. È progettato come modulo autonomo, senza dipendenza dalla GUI, e interagisce solo tramite le interfacce comuni. Può essere eseguito da console e fornisce log dettagliati delle attività processate e simulate.

Questa organizzazione modulare riflette un approccio scalabile e professionale, facilitando la sostituzione o l’estensione di ciascun componente (es. nuova GUI, core alternativo, nuovi test automatizzati) grazie al basso accoppiamento, mantenendo al contempo un forte isolamento tra le responsabilità grazie alla alta coesione.


### Modalità di Funzionamento del Firewall

Il progetto prevede che i pacchetti generati dai client vengano analizzati dal firewall attraverso un meccanismo semplificato ma efficace, basato sull’invocazione esplicita e diretta del metodo ```activeRuleSetProcessPacket()``` da parte di ciascun client, ovvero tramite **chiamata di metodo diretta**.

In fase di progettazione, è stato valutato anche un meccanismo più realistico, in cui i client avrebbero inviato pacchetti tramite socket TCP al firewall, che li avrebbe ricevuti in modo asincrono, ovvero tramite **comunicazione via rete**. Tuttavia, questa soluzione avrebbe introdotto complessità tecniche legate alla gestione della concorrenza sulle connessioni di rete, alla serializzazione dei pacchetti e alla necessità di un ciclo di ricezione attiva (socket polling) da parte del firewall.

Per rendere il progetto più chiaro e focalizzato sugli aspetti centrali (filtraggio, regole, sincronizzazione tra thread e AOP), si è deciso di optare per la prima soluzione, in cui ogni thread client, simulando l’invio del pacchetto, chiama direttamente ```firewall.activeRuleSetProcessPacket(packet)```. Questo approccio consente di simulare efficacemente il flusso dei pacchetti verso il firewall, mantenendo il controllo sulla concorrenza e semplificando il tracciamento e la verifica dell’intero sistema.

Questa decisione preserva l’obiettivo didattico di simulare un ambiente concorrente e reattivo, mantenendo il codice più leggibile, manutenibile e testabile. Inoltre, grazie all’estensivo utilizzo delle interfacce, è possibile sviluppare anche la logica alternativa basata sulla comunicazione via rete e con costi minimi è possibile sostituirla all’implementazione attuale.


### Licenza

Questo progetto è concesso in uso con la licenza MIT, vedere [LICENSE.txt](./LICENSE.txt) per ulteriori informazioni.