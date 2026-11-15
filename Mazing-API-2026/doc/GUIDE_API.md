# Mazing — Guide de l'API

Mazing est une bibliothèque pour afficher les éléments d'un jeu isométrique.
L'API s'utilise à partir de `fr.ubordeaux.ao.mazing.Scene` et d'objets
visuels fournis par `fr.ubordeaux.ao.mazing.view.entity.*`.

## 1. Créer et afficher une scène

Une scène représente une carte en vue isométrique, composée de tuiles. Elle peut être affichée dans une fenêtre autonome ou intégrée dans une interface Swing.

Exemple
```java
import fr.ubordeaux.ao.mazing.Scene;

Scene scene = new Scene(960, 640, 10, 10);
scene.show();
scene.startAnimation(24);
```

`Scene(int width, int height, int mapWidth, int mapHeight)` prend :

- `width`, `height` : dimensions initiales de l'affichage, en pixels ;
- `mapWidth`, `mapHeight` : dimensions de la carte, en nombre de tuiles.

La carte est centrée dans la zone d'affichage, y compris après un redimensionnement.

`show()` ouvre une fenêtre autonome. `close()` la ferme et arrête les
animations. `startAnimation(fps)` fait progresser les animations à la cadence
demandée et rafraîchit leur affichage ; `stopAnimation()` arrête cette horloge.

## 2. Intégrer la scène dans un `JPanel`

La méthode `scene.getPanel()` renvoie le `JPanel` contenant l'affichage de la scène. Il peut être intégré dans une fenêtre Swing avec d'autres composants (`JButton`, `JLabel`, etc.).

```java
JFrame fenetre = new JFrame("Mazing");
JPanel contenu = new JPanel(new BorderLayout());

contenu.add(scene.getPanel(), BorderLayout.CENTER);
contenu.add(new JButton("Nouvelle partie"), BorderLayout.SOUTH);

fenetre.setContentPane(contenu);
fenetre.pack();
fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);fenetre.setVisible(true);

scene.startAnimation(24);
```

Deux modes d'affichage sont possibles :
- `scene.show()` : affiche la scène dans sa propre fenêtre.
- `scene.getPanel()` : permet de l'intégrer dans une interface Swing existante.

Ces deux modes sont alternatifs.

**Important** : les composants Swing doivent être manipulés sur l'EDT (*Event Dispatch Thread*), le thread qui gère l'interface graphique. `SwingUtilities.invokeLater()` permet d'y exécuter le code en toute sécurité.

## 3. Méthodes de `Scene`

| Méthode | Utilisation |
|---|---|
| `addRenderable(Renderable)` | Ajoute un élément graphique ; renvoie `boolean` |
| `addRenderables(List<Renderable>)` | Ajoute plusieurs éléments ; renvoie `boolean` |
| `removeRenderable(Renderable)` | Retire un élément ; renvoie `boolean` |
| `clearRenderables()` | Retire tous les éléments |
| `moveScene(int dx, int dy)` | Déplace la caméra en pixels écran |
| `centerMap()` | Recentre la carte |
| `setZoom(int zoom)` | Modifie le zoom (entier strictement positif) |
| `startAnimation(int fps)` | Démarre ou redémarre la progression automatique des animations et leur rafraîchissement ; `fps` doit être positif |
| `stopAnimation()` | Arrête la progression automatique |
| `animationTick()` | Avance les animations d'un tick et déclenche les éventuels rappels de frame, sans redessiner |
| `repaint()` | Demande un nouveau rendu sans faire progresser les animations |
| `getPanel()` | Renvoie le composant Swing de la scène |
| `show()` | Ouvre la fenêtre autonome |
| `close()` | Ferme cette fenêtre et arrête l'animation |

`Renderable` est l'interface commune aux objets graphiques que l'on peut
ajouter à une scène. Le zoom est un **diviseur** : `setZoom(2)` affiche les
sprites deux fois plus petits que `setZoom(1)`.

### Avancement des animations

Deux modes d'utilisation sont possibles :

- **Automatique :** `scene.startAnimation(24)` fait progresser les animations
  à une cadence d'environ 24 Hz et demande le rafraîchissement de la scène.
  `scene.stopAnimation()` arrête cette progression.
- **Manuel :** l'application appelle `scene.animationTick()` à la cadence
  souhaitée, puis `scene.repaint()` pour afficher les nouvelles frames.

`animationTick()` actualise les animations des objets présents dans la scène,
y compris ceux qui se trouvent hors de la zone visible. Les rappels enregistrés
avec `setAnimationFrameListener(...)` sont exécutés lors des changements de
frame. `repaint()` ne fait pas avancer les animations et ne déclenche pas ces
rappels.

Ne pas appeler manuellement `animationTick()` pendant que
`startAnimation(...)` pilote la même scène.

## 4. Éléments graphiques

Classes du package `fr.ubordeaux.ao.mazing.view.entity` :

| Classe | Rôle | Constructeur courant |
|---|---|---|
| `FloorTile` | Tuile de sol | `FloorTile(Sprite, Orientation, int x, int y, int z)` |
| `Entity` | Objet statique (position entière ou fractionnaire, échelle ajustable) | `Entity(Sprite, Orientation, double x, double y, double z, double scale)` |
| `OccludingEntity` | Mur ou structure pouvant masquer un personnage | `OccludingEntity(Sprite, Orientation, int x, int y, int z)` |
| `AnimatedEntity` | Personnage ou objet animé | `AnimatedEntity(AnimatedOrientedSprite, Orientation, int x, int y, int z)` |

Exemple d'ajout d'un sol et d'un mur :

```java
scene.addRenderable(new FloorTile(
    IsometricSprites.STONE, Orientation.NORTH, 2, 4, 0));
scene.addRenderable(new OccludingEntity(
    IsometricSprites.STONE_WALL, Orientation.NORTH, 2, 3, 0));
```

Pour `Entity` et ses variantes, les méthodes utiles sont :

| Méthode | Utilisation |
|---|---|
| `setPrecisePosition(double x, double y)` | Change la position dans le plan |
| `setPreciseZ(double z)` | Change l'altitude |
| `moveBy(double dx, double dy)` | Déplace l'objet relativement |
| `setOrientation(Orientation)` | Change son orientation graphique |
| `setScale(double scale)` | Modifie sa taille à l'écran |
| `setSprite(Sprite)` | Change son apparence |
| `setRenderOffset(int x, int y)` | Décale visuellement le sprite en pixels source |

### Placer librement un décor avec `Entity`

`Entity` accepte les positions entières ou fractionnaires sur les trois axes.
Les coordonnées `x` et `y` sont exprimées en unités de tuile, et `z` en
niveaux d'altitude. Le paramètre facultatif `scale` modifie uniquement la
taille affichée, sans déplacer l'objet : `1.0` conserve la taille d'origine.
Il doit être strictement positif et fini.

```java
// Une chaise entre les cases de la grille, affichée à 120 % de sa taille.
Entity chaise = new Entity(
    IsometricSprites.CHAIR, Orientation.NORTH,
    2.5, 3.75, 0.0, 1.2);
scene.addRenderable(chaise);
```

Sans taille personnalisée, omettre le dernier argument :

```java
Entity caisse = new Entity(
    IsometricSprites.WOODEN_CRATE, Orientation.NORTH,
    4.25, 2.5, 0.0);
scene.addRenderable(caisse);
```

Pour un placement sur une case entière, le constructeur avec `int x`, `int y`
et `int z` reste disponible. L'orientation est obligatoire pour les
constructeurs à coordonnées fractionnaires ; utiliser `Orientation.NORTH`
si aucune direction particulière n'est nécessaire.

## 5. `AnimatedEntity`

```java
AnimatedEntity personnage = new AnimatedEntity(
    AnimatedSprites.CRUSADER_IDLE, Orientation.SOUTH, 2, 3, 0);
scene.addRenderable(personnage);
```

Constructeurs disponibles :

```java
new AnimatedEntity(sprite);
new AnimatedEntity(sprite, orientation);
new AnimatedEntity(sprite, x, y, z);
new AnimatedEntity(sprite, orientation, x, y, z);
```

`AnimatedEntity` affiche automatiquement l'ombre du personnage si son
sprite en possède une.

| Méthode | Utilisation |
|---|---|
| `setPosition(double x, double y, double z)` | Place le personnage à une position précise |
| `moveBy(double dx, double dy)` | Le déplace relativement |
| `moveInDirection(double dx, double dy)` | Le déplace et adapte son orientation sur les axes principaux |
| `setOrientation(Orientation)` | Change l'orientation |
| `setScale(double scale)` | Modifie la taille affichée |
| `setSprite(Sprite)` | Change l'animation et la redémarre ; attend un `AnimatedOrientedSprite` |
| `getSprite()` | Renvoie le sprite utilisé |
| `setAnimationSpeed(double speed)` | Change la vitesse de lecture |
| `setAnimationPaused(boolean paused)` | Met en pause ou reprend l'animation |
| `setAnimationFrameListener(IntConsumer listener)` | Reçoit l'indice des nouvelles frames lors des ticks d'animation |

```java
if (personnage.getSprite() != AnimatedSprites.CRUSADER_WALK) {
    personnage.setSprite(AnimatedSprites.CRUSADER_WALK);
}
personnage.moveBy(0.1, 0.0);
```

Ne pas appeler `setSprite()` à chaque rafraîchissement si l'animation ne
change pas : l'appel réinitialise sa lecture. Avec
`setAnimationSpeed(0.5)`, elle joue deux fois moins vite ; avec `1.5`, une
fois et demie plus vite. La vitesse d'animation ne modifie pas le déplacement.

## 6. Sprites et orientations

Dans `fr.ubordeaux.ao.mazing.view.sprite` :

- `IsometricSprites` fournit notamment `STONE`, `PLANKS`, `CHAIR`,
  `STONE_WALL`, `STONE_WALL_CORNER`, `STAIRS` et `STAIRS_SPIRAL` ;
- `AnimatedSprites` fournit `CRUSADER_IDLE`, `CRUSADER_WALK`,
  `CRUSADER_RUN`, `CRUSADER_ATTACK`, `CRUSADER_BLOCK`, `CRUSADER_HIT`,
  `CRUSADER_JUMP`, `CRUSADER_DEATH`, `SPIDER_WALK`, `SPIDER_ATTACK`
  et `SPIDER_DEATH`.

`fr.ubordeaux.ao.mazing.view.Orientation` définit `NORTH`, `SOUTH`,
`EAST`, `WEST`, `NORTH_EAST`, `NORTH_WEST`, `SOUTH_EAST` et `SOUTH_WEST`.

## 7. Ajouter des sons

Les sons se déclenchent indépendamment de la scène : il ne faut pas les passer à
`scene.addRenderable(...)`. Le package `fr.ubordeaux.ao.mazing.view.sound`
fournit des effets prêts à l'emploi, **synthétisés par la bibliothèque** :
aucun fichier WAV n'est nécessaire pour les utiliser.

### 7.1. Utiliser les effets sonores intégrés

| Classe | Méthodes de lecture | Effets |
|---|---|---|
| `FootstepSound` | `play()` | Un bruit de pas |
| `CrusaderCombatSounds` | `playAttack()`, `playHit()`, `playDeathCry()`, `playDeathExhale()` | Attaque, coup reçu, début et fin de l'animation de mort |
| `CrusaderStaticSounds` | `playJumpTakeoff()`, `playJumpLanding()`, `playIdleBreath()`, `playShieldBlock()` | Décollage et réception d'un saut, respiration, bouclier |

Créer les objets **une seule fois**, au démarrage du jeu, puis appeler la
méthode correspondant à l'action à son déclenchement :

```java
import fr.ubordeaux.ao.mazing.view.sound.FootstepSound;
import fr.ubordeaux.ao.mazing.view.sound.CrusaderCombatSounds;
import fr.ubordeaux.ao.mazing.view.sound.CrusaderStaticSounds;

FootstepSound pas = new FootstepSound();
CrusaderCombatSounds combat = new CrusaderCombatSounds();
CrusaderStaticSounds mouvements = new CrusaderStaticSounds();

pas.play();                     // Un pas
combat.playAttack();           // Une attaque
combat.playHit();              // Un coup reçu
mouvements.playJumpTakeoff();  // Début d'un saut
mouvements.playJumpLanding();  // Atterrissage
mouvements.playShieldBlock();  // Blocage au bouclier
```

La lecture est non bloquante. Il faut conserver les instances tant que le jeu
utilise ces sons et libérer leurs ressources audio **à la fermeture du jeu** :

```java
// À exécuter quand le jeu se termine, pas immédiatement après play().
pas.close();
combat.close();
mouvements.close();
```

Les trois classes implémentent `AutoCloseable`. Leur création peut échouer
avec une `IllegalStateException` si le système ne dispose pas d'une sortie
audio utilisable.

### 7.2. Jouer un son au bon moment dans une animation

Pour synchroniser un son avec une animation, on peut utiliser
`setAnimationFrameListener(...)` sur un `AnimatedEntity`. Le rappel reçoit
l'indice de la nouvelle frame lorsque les animations avancent via
`scene.startAnimation(...)` ou `scene.animationTick()` :

```java
import fr.ubordeaux.ao.mazing.view.Orientation;
import fr.ubordeaux.ao.mazing.view.entity.AnimatedEntity;
import fr.ubordeaux.ao.mazing.view.sprite.AnimatedSprites;

AnimatedEntity personnage = new AnimatedEntity(
    AnimatedSprites.CRUSADER_WALK, Orientation.SOUTH, 2, 3, 0);
scene.addRenderable(personnage);

// « pas » est le FootstepSound créé plus haut.
personnage.setAnimationFrameListener(frame -> {
    if (personnage.getSprite() == AnimatedSprites.CRUSADER_WALK
            && frame == 0) {
        pas.play(); // Un son à chaque retour au début du cycle de marche.
    }
});

scene.startAnimation(24);
```

L'indice de frame à choisir dépend de l'animation et du moment où l'effet doit
se produire. Pour les événements déclenchés par le joueur (attaque, saut,
blocage, etc.), appeler plutôt la méthode `play...()` **au moment de l'action**,
par exemple lorsque le programme lance `CRUSADER_ATTACK` :

```java
personnage.setSprite(AnimatedSprites.CRUSADER_ATTACK);
combat.playAttack();
```

`setSprite(...)` redémarre l'animation ; ne pas le rappeler à chaque
rafraîchissement si le sprite ne change pas. Éviter également de jouer un son
à chaque frame, au risque de le répéter trop souvent.

### 7.3. Ajouter un fichier son personnalisé (WAV)

Les classes précédentes produisent des effets prédéfinis : elles **ne
chargent pas** les fichiers audio de l'application. Pour jouer son propre son,
on peut utiliser l'API audio standard de Java (`javax.sound.sampled`).

1. Ajouter un fichier WAV (de préférence au format PCM) dans
   `src/main/resources/sounds/porte.wav`.
2. Gradle ajoute automatiquement `src/main/resources/` au **classpath** lors de
   la compilation et de l'exécution. Dans une application externe qui utilise
   la distribution binaire, ajouter le fichier dans les ressources de cette
   application et s'assurer qu'elles figurent dans le classpath.
3. Charger le fichier une fois, puis déclencher sa lecture à chaque action.

```java
import java.util.Objects;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

// À l'initialisation : « MonProgramme » est la classe de votre application.
Clip porte = AudioSystem.getClip();
try (AudioInputStream audio = AudioSystem.getAudioInputStream(
        Objects.requireNonNull(
                MonProgramme.class.getResource("/sounds/porte.wav")))) {
    porte.open(audio);
}

// Lorsqu'une porte s'ouvre :
porte.stop();
porte.setFramePosition(0);
porte.start();

// À la fermeture du jeu (après la fin des lectures) :
porte.close();
```

Le chemin `/sounds/porte.wav` est relatif à la racine du classpath,
indépendamment de l'emplacement du fichier Java qui déclenche le son.
Si le fichier est absent, `getResource(...)` renvoie `null` et
`requireNonNull(...)` signale l'erreur.

Les opérations de chargement peuvent lever des exceptions audio et d'E/S ;
les gérer ou les déclarer (`throws Exception`) dans la méthode concernée.
Ne pas fermer le `Clip` immédiatement après `start()` : la fermeture coupe
la lecture. Pour jouer plusieurs fois le même son, conserver ce `Clip` ouvert
et le replacer au début avant chaque lecture.
