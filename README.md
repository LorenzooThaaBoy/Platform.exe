# Platformer THE GAME 

## Game idea 
Platformer THE GAME is a small 2D arena platformer. The player moves across a compact platform map, jumps between ledges, and fights enemies with a short melee attack while trying to stay alive with a limited number of lives.

The current idea is to grow the game into round-based survival: enemies spawn from different sides of the map, chase the player, and deal damage on contact. After surviving rounds, the player should be able to enter a shop and buy upgrades such as more damage, more range, extra HP, more speed, or a dash.

## Development 
### Code Strucutre
We deployed the MCV pattern for easy class overview aswell as it being industry standart for games. Easy debugging and a nice layer overwiev what makes it easy to adjust. The code for this project can be found in "core/src/main/java/io/github/some_example_name" (ik its bad, we'll fix it, probably)

//TODO:
- Excel Screenshot 
- Arbeitsteilung 
- Rollen
- Ai acknowlegment 
- Game Mechanicks explanantion (depth) 
- Game Mechanicks + maps screenshot 

## LibGDX Information 
A [libGDX](https://libgdx.com/) project generated with [gdx-liftoff](https://github.com/libgdx/gdx-liftoff).

This project was generated with a template including simple application launchers and an `ApplicationAdapter` extension that draws libGDX logo.

### Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3; was called 'desktop' in older docs.

### Gradle

This project uses [Gradle](https://gradle.org/) to manage dependencies.
The Gradle wrapper was included, so you can run Gradle tasks using `gradlew.bat` or `./gradlew` commands.
Useful Gradle tasks and flags:

- `--continue`: when using this flag, errors will not stop the tasks from running.
- `--daemon`: thanks to this flag, Gradle daemon will be used to run chosen tasks.
- `--offline`: when using this flag, cached dependency archives will be used.
- `--refresh-dependencies`: this flag forces validation of all dependencies. Useful for snapshot versions.
- `build`: builds sources and archives of every project.
- `cleanEclipse`: removes Eclipse project data.
- `cleanIdea`: removes IntelliJ project data.
- `clean`: removes `build` folders, which store compiled classes and built archives.
- `eclipse`: generates Eclipse project data.
- `idea`: generates IntelliJ project data.
- `lwjgl3:jar`: builds application's runnable jar, which can be found at `lwjgl3/build/libs`.
- `lwjgl3:run`: starts the application.
- `test`: runs unit tests (if any).

Note that most tasks that are not specific to a single project can be run with `name:` prefix, where the `name` should be replaced with the ID of a specific project.
For example, `core:clean` removes `build` folder only from the `core` project.


## Installation Guide
### MacOs
### Windows
### Linux (if supported) 
