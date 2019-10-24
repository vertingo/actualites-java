# À propos de l'application Actualités

* Licence : [AGPL v3](http://www.gnu.org/licenses/agpl.txt) - Copyright Conseil Régional Nord Pas de Calais - Picardie, Conseil départemental de l'Essonne, Conseil régional d'Aquitaine-Limousin-Poitou-Charentes
* Développeur(s) : ATOS
* Financeur(s) : Région Nord Pas de Calais-Picardie,  Département 91, Région Aquitaine-Limousin-Poitou-Charentes

* Description : Application de diffusion d'actualités organisées en fil

# Documentation technique
## Construction

<pre>
		gradle copyMod
</pre>

## Déployer dans ent-core


## Configuration

Dans le fichier `/actualites/deployment/actualites/conf.json.template` :


Déclarer l'application dans la liste :
<pre>
    {
      "name": "net.atos~actualites~0.4-SNAPSHOT",
      "config": {
        "main" : "net.atos.entng.actualites.Actualites",
        "port" : 8022,
		"sql" : true,
        "mongodb" : false,
        "neo4j" : false,
        "app-name" : "Actualites",
        "app-address" : "/actualites",
        "app-icon" : "actualites-large",
        "host": "${host}",
        "ssl" : $ssl,
        "auto-redeploy": false,
        "userbook-host": "${host}",
        "integration-mode" : "HTTP",
        "app-registry.port" : 8012,
        "mode" : "${mode}",
        "entcore.port" : 8009
      }
    }
</pre>


Associer une route d'entée à la configuration du module proxy intégré (`"name": "net.atos~actualites~0.4-SNAPSHOT"`) :
<pre>
	{
		"location": "/actualites",
		"proxy_pass": "http://localhost:8022"
	}
</pre>


Déclarer l'application dans la liste des widgets :
<pre>
	{
		"name": "actualites",
		"path": "/actualites/public/template/last-infos-widget.html",
		"js": "/actualites/public/js/last-infos-widget.js",
		"i18n": "/actualites/i18n"
	}
</pre>


# Présentation du module

## Fonctionnalités

Actualités est une application de création, de publication et de consultation d'actualités.
Les actualités sont organisées en fils d'actualités (ou Catégories).
Les actualités sont soumises à un workflow simple de publication.
Elles ont donc un état ("Brouillon", "En attende de validation", "Publiée") qui conditionne leur visibilité finale dans le fil.
Des dates de publication et d'expiration peuvent de plus être définies.
Des permissions sur les différentes actions possibles sur les fils d'actualités, dont la publication, sont configurées dans ces fils (via des partages Ent-core).
Le droit de lecture/commentaire, correspondant à qui peut consulter/commenter les actualités est également configuré de cette manière.
Les actualités mettent en œuvre un comportement de recherche sur le titre et le contenu de celles-ci.

## Modèle de persistance

Les données du module sont stockées dans une base PostgreSQL, dans le schéma `actualites`.
Les scripts sql se trouvent dans le dossier "src/main/resources/sql".

3 tables représentent le modèle relationnel applicatif :
 * `thread` : Fil d'actualités
 * `info` : Actualité
 * `comment` : Commentaire d'actulités
 
Une `info` appartient à un `thread` et un `thread` peut avoir plusieurs objects `info`.
Un `comment` appartient à une `info` et une `info` peut avoir plusieurs objects `comment`.

Les tables `thread` et `info` sont liées à des tables de partage pour implémenter le fonctionnement du framework Ent-core
 * `thread_shares`
 * `info_shares`

## Modèle serveur

Le module serveur utilise 4 contrôleurs de déclaration :
 * `DisplayController` : Point d'entrée à l'application
 * `ThreadController` : Routage des vues, sécurité globale et déclaration des APIs de manipulation des fils d'actualités (Thread)
 * `InfoController` : Routage des vues, sécurité globale et déclaration des APIs de manipulation des actualités (Info)
 * `CommentController` : Routage des vues, sécurité globale et déclaration des APIs de manipulation des commentaires (Comment)
Et 2 filters :
 * `ThreadFilter` : sécurité d'accès aux Fils d'actualités (Thread)
 * `InfoFilter` : sécurité d'accès aux actualités (Info)

Les contrôleurs étendent les classes du framework Ent-core exploitant les CrudServices de base.
Pour manipulations spécifiques, des classes de Service sont utilisées :
 * `ThreadService` : concernant les fils d'actualités
 * `InfoService` : concernant les Actualités

 Le module serveur met en œuvre deux évènements issus du framework Ent-core  :
 * `ActualitesRepositoryEvents` : Logique de changement d'année scolaire
 * `ActualitesSearchingEvents` : Logique de recherche


Des jsonschemas permettent de vérifier les données reçues par le serveur, ils se trouvent dans le dossier "src/main/resources/jsonschema".

## Modèle front-end

Le modèle Front-end manipule 2 objets model :
 * `Thread` correspondant à un fil d'actualités.
 * `Info` correspondant à une Actualité. Comprenant un champ `status` représentant l'état de publication.

Il y a 2 Collections globales :
 * `model.threads` : objets `Thread` synchronisée depuis le serveur.
 * `model.infos` : objets `Info` synchronisée depuis le serveur.


