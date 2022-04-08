# TP Queues

## Installation 


remplacer les crédentials AccountName & AccountKey dans le fichier App.java : 
```
final String connectStr =
                "DefaultEndpointsProtocol=https;" +
                        "AccountName= myAccountName" +
                        "AccountKey= myaccountKey";            
```

Dans le répertoire /src lancer les commandes suivantes :
```
mvn clean install
```

```
mvn exec:java -Dexec.mainClass="com.queues.howto.App"
```
## Code
Le travaille est disponible dans le fichier App.java 

Déroulement du programme => 
l'on crée 3 Queues : 1 Queue producteur et 2 Queues consommateur 1 et 2.

dans le premier processus l'on envoie 100 messages dans la queue producteur.

dans les 2 autres processus l'on récupère les messages du producteur puis nous les modifions afin des traiter et les enregistrer dans 
les processus consommateurs, le message initial est supprimé du producteur.
