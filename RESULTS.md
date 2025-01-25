# Résultats des simulations de Monte Carlo sur le paradoxe des anniversaires

**Auteurs :** Jarod Streckeisen, Timothée Van Hove

Ce document présente les résultats des simulations effectuées, conformément aux consignes du projet.



## Simulation 1 : Estimation de p23

### Objectif
Estimer la probabilité $p_{23}$ que, dans un groupe de 23 personnes, au moins deux partagent la même date d'anniversaire.

La simulation a été répétée avec une précision croissante (diminution de la demi-largeur de l'intervalle de confiance à chaque itération).

### Résultats

| Itération | Probabilité estimée | Demi-largeur | Intervalle de confiance | Nombre total de simulations | Temps d'exécution |
| --------- | ------------------- | ------------ | ----------------------- | --------------------------- | ----------------- |
| 1         | 0.507267            | 0.000100     | [0.507167, 0.507367]    | 96'100'000                  | 21 s              |
| 2         | 0.507313            | 0.000050     | [0.507263, 0.507363]    | 384'100'000                 | 1 min 22 s        |
| 3         | 0.507311            | 0.000025     | [0.507286, 0.507336]    | 1'536'300'000               | 5 min 33 s        |



## Simulation 2 : Seuil de couverture des intervalles

### Objectif
Calculer le pourcentage d'intervalles de confiance (au seuil de 95 %) contenant la valeur théorique de $p_{23} = 0.5072972343$ à partir de 1'000 répétitions, chacune utilisant un échantillon de 1'000'000 de réalisations.

### Résultats

| Résultat                       | Valeur           |
| ------------------------------ | ---------------- |
| Seuil empirique de couverture  | 0.9540           |
| Intervalle de confiance (95 %) | [0.9410, 0.9670] |
| Temps d'exécution              | 3 min 35 s       |



## Simulation 3 : Recherche de K minimal

### Objectif
Déterminer le plus petit nombre $K$ de personnes nécessaire pour qu'au moins trois partagent la même date d'anniversaire avec une probabilité supérieure à 0.5.

### Résultats

| $K$    | Probabilité estimée | Intervalle de confiance  |
| ------ | ------------------- | ------------------------ |
| 80     | 0.418477            | [0.417510, 0.419444]     |
| 81     | 0.430291            | [0.429321, 0.431261]     |
| 82     | 0.439957            | [0.438984, 0.440930]     |
| 83     | 0.452691            | [0.451715, 0.453667]     |
| 84     | 0.464401            | [0.463424, 0.465378]     |
| 85     | 0.476246            | [0.475267, 0.477225]     |
| 86     | 0.488008            | [0.487028, 0.488988]     |
| 87     | 0.499544            | [0.498564, 0.500524]     |
| **88** | **0.511525**        | **[0.510545, 0.512505]** |

- **K minimal trouvé : 88**
- **Temps d'exécution : 5 s**



## Résumé des temps d'exécution

| Simulation             | Temps total     |
| ---------------------- | --------------- |
| Simulation 1           | 7 min 16 s      |
| Simulation 2           | 3 min 35 s      |
| Simulation 3           | 5 s             |
| **Temps total global** | **10 min 56 s** |



## Sortie console du programme

```
=== Simulation 1 : Estimation de p23 ===
Itération 1 :
  Probabilité estimée            : 0.507267
  Demi-largeur de l'intervalle   : 0.000100
  Intervalle de confiance        : [0.507167, 0.507367]
  Nombre total de simulations    : 96100000
  Temps d'exécution              : 21 s

Itération 2 :
  Probabilité estimée            : 0.507313
  Demi-largeur de l'intervalle   : 0.000050
  Intervalle de confiance        : [0.507263, 0.507363]
  Nombre total de simulations    : 384100000
  Temps d'exécution              : 1 min 24 s

Itération 3 :
  Probabilité estimée            : 0.507311
  Demi-largeur de l'intervalle   : 0.000025
  Intervalle de confiance        : [0.507286, 0.507336]
  Nombre total de simulations    : 1536300000
  Temps d'exécution              : 5 min 30 s


=== Simulation 2 : Seuil de couverture des intervalles ===
  Seuil empirique de couverture  : 0.9540
  Ic pour la couverture          : [0.9410, 0.9670]
  Temps d'exécution              : 3 min 34 s


=== Simulation 3 : Recherche de K minimal ===
Pour K = 80 :
  Probabilité estimée            : 0.418477
  Intervalle de confiance        : [0.417510, 0.419444]
Pour K = 81 :
  Probabilité estimée            : 0.430291
  Intervalle de confiance        : [0.429321, 0.431261]
Pour K = 82 :
  Probabilité estimée            : 0.439957
  Intervalle de confiance        : [0.438984, 0.440930]
Pour K = 83 :
  Probabilité estimée            : 0.452691
  Intervalle de confiance        : [0.451715, 0.453667]
Pour K = 84 :
  Probabilité estimée            : 0.464401
  Intervalle de confiance        : [0.463424, 0.465378]
Pour K = 85 :
  Probabilité estimée            : 0.476246
  Intervalle de confiance        : [0.475267, 0.477225]
Pour K = 86 :
  Probabilité estimée            : 0.488008
  Intervalle de confiance        : [0.487028, 0.488988]
Pour K = 87 :
  Probabilité estimée            : 0.499544
  Intervalle de confiance        : [0.498564, 0.500524]
Pour K = 88 :
  Probabilité estimée            : 0.511525
  Intervalle de confiance        : [0.510545, 0.512505]

  ** K minimal trouvé : 88 **
  Temps d'exécution              : 5 s
```

