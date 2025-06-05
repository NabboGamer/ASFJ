# Software Firewall

Repository del progetto "Software Firewall" sviluppato per la prova finale di Tecniche Avanzata di Programmazione, edizione 2024-2025, Università degli Studi della Basilicata, laurea magistrale in Ingegneria informatica e delle tecnologie dell'informazione.

## Introduzione

## Architettura del progetto

Il progetto è stato strutturato in modo modulare per favorire la manutenibilità, la testabilità e la riusabilità del codice. Sono stati definiti quattro sottoprogetti principali, organizzati secondo principi solidi di sviluppo delle moderne architettura software come separazione delle responsabilità, inversione del controllo e isolamento dei componenti:

1) **firewall-api**: contiene le interfacce condivise tra i moduli, come IFirewall, IRuleSet, IRule, IPacket ecc. Questo modulo rappresenta il contratto che lega insieme gli altri componenti e consente un disaccoppiamento chiaro tra implementazioni e dipendenze.

2) **firewall-core**: implementa la logica centrale del firewall, comprese le regole di filtraggio, il controllo dei pacchetti e la gestione del RuleSet. È progettato per essere indipendente dall'interfaccia utente, rendendolo facilmente testabile e riutilizzabile in diversi contesti (es. interfacce grafiche alternative, servizi, test automatici).

3) **firewall-gui**: fornisce l'interfaccia grafica Desktop Swing per l’interazione con il firewall. Si appoggia unicamente alle interfacce definite in firewall-api per interagire con la logica sottostante, ed è conforme con l’architettura Model-View-Controller (MVC). La GUI consente la visualizzazione dei pacchetti bloccati, la modifica “live” delle regole e la gestione interattiva del firewall.

4) **client-simulator**: simula il comportamento di client multipli che inviano pseudopacchetti concorrenti verso il firewall. È progettato come modulo autonomo, senza dipendenza dalla GUI, e interagisce solo tramite le interfacce comuni. Può essere eseguito da console e fornisce log dettagliati delle attività processate e simulate.

Questa organizzazione modulare riflette un approccio scalabile e professionale, facilitando la sostituzione o l’estensione di ciascun componente (es. nuova GUI, firewall alternativo, nuovi test automatizzati), mantenendo al contempo un forte isolamento tra le responsabilità(alta coesione) e un basso accoppiamento.


## Modalità di Funzionamento del Firewall

Il progetto prevede che i pacchetti generati dai client vengano analizzati dal firewall attraverso un meccanismo semplificato ma efficace, basato sull’invocazione esplicita del metodo ```inspect()``` da parte di ciascun client ovvero un **Modello Pull** (passivo).
In fase di progettazione, è stato valutato un approccio più realistico basato su un **Modello Push** (attivo con socket polling), in cui i client avrebbero inviato pacchetti tramite socket TCP verso il firewall, che li avrebbe ricevuti in modo asincrono. Tuttavia, questo avrebbe introdotto complessità tecniche legate alla gestione della concorrenza sulle connessioni di rete, alla serializzazione dei pacchetti, e alla necessità di un ciclo di ricezione attiva (polling) da parte del firewall.
Per rendere il progetto più chiaro, focalizzato sugli aspetti centrali (filtraggio, regole, sincronizzazione tra thread e AOP), si è deciso di optare per una soluzione pull esplicita, in cui ogni client thread, simulando l’invio del pacchetto, chiama direttamente ```firewall.inspect(packet)```. Questo consente di simulare efficacemente il flusso dei pacchetti verso il firewall, mantenendo il controllo sulla concorrenza e semplificando il tracciamento e la verifica dell’intero sistema.
Questa decisione preserva l’obiettivo didattico di simulare un ambiente concorrente e reattivo, mantenendo il codice più leggibile, manutenibile e testabile.


## Licenza

Questo progetto è concesso in uso con la licenza MIT, vedere [LICENSE.txt](./LICENSE.txt) per ulteriori informazioni.