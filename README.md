# Serveur-Web-Reseaux

## Lancer le serveur
Pour lancer le serveur,  il faut lancer le .bat qui se situe dans target/appassembler/bin

## Fonctionnement
Le programme va d'abord lire le contenu du fichier properties.txt
Dedans, vous pouvez renseigner le répertoire racine où mettre les sites web et le port d'écoute du serveur.

Le serveur va ensuite écouter sur le PORT défini (80 par défaut) et attendre qu'il y ait une connection.
Dès qu'un nouveau client est connecté, on l'affiche.

On lance ensuite un thread pour chaque connexion entrante.

La classe Request.java va gérer la requête du client.
On récupère d'abord l'ensemble de la requête puis on la parse pour obtenir les informations qui nous sont utiles.
On récupère la méthode, pour s'assurer que c'est bien un GET.
On récupère la requête, c'est à dire la page web demandée par le client
On récupère l'host, c'est à dire le domaine.

On va ensuite parser le domaine pour connaitre le sous-domaine, le nom du domaine et le tld.
Pour cela, on parse le host à chaque point et on récupère chaque élément.
On va ensuite reconstruire le chemin vers le fichier avec ces éléments

Une fois le chemin construit, on vérifie si le fichier en question existe. S'il n'existe pas, on renvoi une erreur 404.

Si la méthode n'est pas une méthode GET, on ne la gère pas.

## Format de la requête
Pour que la reqête fonctionne, elle doit obligatoirement finir par un "/"
Si elle fini par un "/", on va alors rajouter "index.html" au chemin vers le fichier pour afficher la page de base

L'utilisateur peut aussi préciser lui-même qu'il souhaite récupérer ce fichier en l'indiquant dans la requête.
Par exemple : 
  localhost:80/verti/index.thml et localhost:80/verti/ afficheront tous les deux la page index.html du site verti

Mais si l'utilisateur rentre : localhost:80/verti, cela ne marchera pas

## Fonctionnalités non-implémentées
La protection d'une ressource par une authentification basique n'a pas été implémentée.
Aucune fonctionnalité bonus n'a été implémentée.

## Bug commum
Il est possible que vous ayez cette erreur : java.net.BindException: Address already in use: bind
Cela signifie que le serveur est déjà lancé et qu'il écoute déjà sur cette adresse. Vous devez donc fermer la connection déjà ouverte sur ce port
# Web-server
