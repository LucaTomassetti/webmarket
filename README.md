# webmarket
Progetto "WebMarket"
Versione 1.0

Specifiche del Sito
Il sito WebMarket rappresenta un sistema di acquisti online simile ai vari eCommerce che tutti ben conosciamo, ma vincolato e guidato, in quanto pensato per essere usato all'interno di un'organizzazione pubblica.

Saranno previste due tipologie di utenza: ordinanti e tecnici. Nel sistema sarà previsto anche un amministratore col solo scopo di registrare altri utenti.

Il sistema funzionerà come segue (ovviamente semplificheremo molti passaggi per evitare di rendere il progetto troppo pesante, quindi alcuni punti saranno poco realistici... se volete siete liberi di renderli più completi e complessi!).

Definizione della richiesta di acquisto. L’ordinante sarà inizialmente guidato nella selezione di una categoria (ad esempio PC Desktop, Notebook, Scrivania,...) che identifichi la tipologia di prodotto da acquistare (opzionalmente le categorie potranno avere una struttura ad albero, ad esempio Informatica > Computer > Notebook). Ogni categoria di prodotto avrà associate una serie di caratteristiche specifiche (ad esempio quantità di RAM e tipo di CPU per un PC, ecc.). L'ordinante dovrà quindi inserire i valori di tutte caratteristiche desiderate relative alla categoria di prodotto selezionata (per ogni caratteristica sarà comunque sempre prevista l'opzione indifferente). Sarà presente anche uno spazio note per aggiungere ogni caratteristica peculiare non annoverata tra quelle standard.

Definizione della proposta di acquisto. La richiesta di acquisto definita al punto 1 sarà inoltrata al personale tecnico, che ne riceverà notifica. Un membro del personale prenderà quindi in carico la richiesta, diventando il tecnico incaricato della richiesta stessa. Il tecnico incaricato potrà visionare la categoria e le caratteristiche richieste dall'ordinante e cercherà (esternamente al sistema) un prodotto effettivo da ordinare (che chiameremo proposta di acquisto), associandone la descrizione (che comprenderà almeno nome produttore, nome prodotto, codice prodotto, prezzo, URL di approfondimento e un campo note) alla richiesta di acquisto.

Revisione della proposta di acquisto. L'ordinante, notificato dell'immissione da parte del tecnico incaricato della proposta relativa a una sua richiesta, ne prenderà visione e potrà approvarla o respingerla, fornendo in questo caso una motivazione. In caso di richiesta respinta, il processo riprenderà dal passo 2, considerando che il tecnico incaricato sarà sempre lo stesso. La motivazione del rifiuto sarà mostrata assieme alle informazioni della richiesta, e il tecnico incaricato potrà modificare il prodotto candidato e inoltrarlo nuovamente all'ordinante.

Esecuzione dell'ordine di acquisto. Nel caso in cui l'ordinante approvi la scelta del prodotto candidato al passo 3, questo verrà notificato al personale tecnico che procederà a definire il relativo ordine di acquisto (questa parte non sarà gestita dal nostro applicativo).

Consegna e verifica dell'ordine di acquisto. Quando il prodotto sarà consegnato (prima o poi!), l'ordinante dovrà chiudere la relativa richiesta di acquisto indicando se il prodotto ricevuto è stato accettato, respinto perché non conforme oppure respinto perché non funzionante.

Di seguito sono illustrati schematicamente i contenuti e le funzionalità minime che dovrebbero essere inseriti nel sito. Ovviamente, ogni ulteriore raffinamento o arricchimento di queste specifiche aumenterà il valore del progetto.

L'accesso all'applicazione sarà riservato solo a utenti registrati. Dovrà essere prevista la figura di un amministratore, il quale avrà la facoltà di registrare nel sistema nuovi ordinanti e nuovi tecnici.

Dopo l'accesso, verrà visualizzata una dashboard contenente

per gli ordinanti, le richieste di acquisto in corso e già chiuse. Le richieste di acquisto per le quali esiste una proposta associata (non ancora accettata) saranno debitamente evidenziate.

per i tecnici, la lista delle richieste non ancora assegnate e di quelle di cui sono incaricati. Le richieste con proposta accettata o respinta saranno debitamente evidenziate.

Gli ordinanti potranno in ogni momento creare una nuova richiesta di acquisto, come indicato al punto 1 della procedura precedente.

I tecnici potranno prendere in carico le richieste non ancora assegnate e, in un secondo momento, inserire i dettagli di una proposta nelle richieste loro assegnate, come descritto al punto 2.

Gli ordinanti potranno visualizzare i dettagli delle loro richieste e dell'eventuale proposta associata, con la facoltà di accettarla o respingerla come descritto al punto 3.

I tecnici potranno marcare una richiesta di acquisto con proposta accettata come "ordinata" (punto 4) per aggiornare l'ordinante sullo stato della sua richiesta (sempre visibile assieme alla richiesta stessa nelle dashboard).

Gli ordinanti, una volta che la loro richiesta sarà posta in stato "ordinato", potranno in qualsiasi momento procedere con la chiusura come descritto al punto 5.

Tutti i passaggi principali appena descritti (inserimento richiesta, presa in carico, inserimento proposta, accettazione o rifiuto della stessa, ordine del prodotto, chiusura della richiesta) dovrebbero essere opportunamente notificati ai soggetti coinvolti, ad esempio per email, oltre che evidenziati nella dashboard degli interessati.
