# Music Cleanup Utility
A simple program written to help me organize my music. 

[Introduction](#introduction) | [Features](#features) | [Tech Stack](#tech-stack) | [Installation](#installation) | [Quick start](#quick-start) | [Known issues and limitations](#known-issues-and-limitations) | [Getting help](#getting-help) | [Contributing](#contributing) | [License](#license)
## Introduction
The program consist of several features aiming to solve different problems I faced. With the flags provided different features can be activated, with no flags provided all of them in use.
## Features
- **COLLECT("--collect")** Collect and copy all audio files (primarily `.mp3` and `.flac`) from the specified `ROOT_MUSIC_DIRECTORY` to the program working directory `mcu/collected` in a flat format - unfolding all folders along the way. All other features are working with this folder as a root folder. You can specify directories for exclusion, these directories are copies as they are to the `mcu/transferred` directory. 

- **OVERVIEW("--overview")** Creates an overview of all tracks showing their main tags in a convenient `.json` format. Here's an example:
```JSON
[{
	"name": "ACDC - Hells Bells.mp3",
	"title": {"old": "Hells Bells", "upd": "-"},
	"artist": {"old": "AC/DC", "upd": "-"},
	"album": {"old": "", "upd": "-"},
	"year": {"old": "0", "upd": "-"},
	"genre": {"old": "", "upd": "-"}
}]
```
Current values lies under the field `old` in every corresponding tag. The feature generates multiple overview files with N (default: 32) songs in every file.
The overview gives you an overall understanding of the tracks' state and it's a useful format to use for the future tag modification. The files live under `mcu/overview`.

- **REPORT("--report")** Provides similar in terms of format to overview report, with some additional info, the most important of which is `breakaged` . The `breakages` field shows how many tags are either `BROKEN` or `EMPTY`. Every tag goes through a dedicated validation to determine whether a tag is a legit one. Reports similarly organized into different files with N (default: 20) songs in each. The format:
```JSON
[{
    "name": "ACDC - Hells Bells.mp3",
    "title": {"old": "Hells Bells"},
    "artist": {"old": "AC/DC"},
    "album": {"old": "", "sts": "EMPTY"},
    "year": {"old": "0", "sts": "BROKEN"},
    "genre": {"old": "", "sts": "EMPTY"},
    "bitrate": {"old": "320"},
    "breakages": 3,
    "cover": {"old": "image/png:3.png:85873"}
}]
```
This format provides a more detailed file state, but only the files that has at least one breakage are reported. The field `upd` is also not present to reduce visual noise. The files live under `mcu/tags_broken`.  


- **MODIFY_TAGS("--modify-tags")** Modifies tags of the previously collected files `mcu/collected` based on the provided set of data in the directory `mcu/tags_new`. The program goes over the folder and matches a song and its metadata in a `.json` file by the field `name` and the actual file name, so don't change the name of a file or change in both cases if you want it to be updated. Format:
```JSON
[{
	"name": "ACDC - Hells Bells.mp3",
	"title": {"old": "Hells Bells", "upd": "Hells Bells"},
	"artist": {"old": "AC/DC", "upd": "AC/DC"},
	"album": {"old": "", "upd": "Back in Black"},
	"year": {"old": "0", "upd": "1980"},
	"genre": {"old": "", "upd": "Hard Rock"}
}]
```
   Modifier takes the relevant`upd` values if it is provided and updated the matching track   metadata.
   Besides the tag update, as a second step, the feature renames all the files in the following pattern: `artist - title` (duplicates are preserved).

- **SORT("--sort")** Sorts audio-tracks into two folders: `artists` and `artists_others` based on the provided `presence threshold` (default: 10). The feature builds a map with an artists as a key, and value as an array of tracks, then, if an artists' presence exceeds the threshold the tracks falls into `artist` category, if not `artists_others`. The program automatically created needed artist-folders putting relevant tracks in them.

- **BREAKDOWN("--breakdown")** Creates a single `.json` file with the collection genre breakdown. 
## Tech stack
- Java 21
- jaudiotagger (a cool library for working with audio files)
- Maven
## Installation
Copy the code from the repository and run it with Maven. 
### Prerequisites
- JVM, Maven
## Quick start
You can use Intellij IDEA or Java extension pack for your VSC and run the program with the provided runner. 
### Configuration
Environment file lives under `src/main/resources/environments.env`. Key settings include:

```
ROOT_MUSIC_DIRECTORY=(absolut path to the desired music folder)
ROOT_WORKING_DIRECTORY=(absolut path to the desired program working folder)
```
## Known issues and limitations
- this program is flawless
## Getting help
If you encounter a bug or have a question about the project, please use the repository issue tracker or reach me out directly.
## Contributing
Pull requests are welcome. For substantial changes, please open an issue first to discuss the proposed approach.
## License
This project is licensed under the MIT License. See the `LICENSE` file for details.

---

Even though the project was created by me without the use of any kind of AI I found it particularly convenient to use it to help with filling up the `tags_new` files with correct values. Here's the message I used to instruct an AI:

	I need your help to find correct audio track metadata. I have prepared a set of .json files that contains tack metadata breakdowns, the problems is that some values are missing or wrong and I need to recover them and get the right parameters. I attached several files so you understand what I'm talking about.

	Here's the instructions:
	- read available information and identify a track, field "old" contains the current value of a tag, you don't alter it, only read
	- sometimes the current value are too broken, so don't hesitate to rear the "name" field, it might help
	- upon getting correct information from the internet, insert it under the corresponding tag to the field "upd" (has value "-" by default as a placeholder), don't leave this field unfilled
	- try to find official information for every tag, avoid making them up. Use widely known information, don't overdo it 
	- if the official source tells that there were several of something "artists", "genres", etc. write them through a comma (e.g. "artist one, artist two" )  from the most to less significant
	- you can use the value "Unknown" if you can't justify a tag in any way
	- name - the name of a file, usually in the format of "artist" - "title", don't change
	- title - title as it is, not much to add
	- artist - artists, or, in some cases an array of artists
	- album - a song's album, might be "Single" or "Soundtrack" or "Special" if there's no an album per se
	- year - release year
	- genre - try to use more specific genre(s) (e.g. "punk rock" instead of "rock"), but if it's too difficult to determine, just keep it simple, and yes, you can add several genres if it makes sense
	- provide me with a prepared and validated downloadable .json file

	Analyze the information and ask questions if you have any before we start.