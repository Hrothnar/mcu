# MCU (Music Cleaning Utility)

This is a utility for organizing music, by finding broken tags, applying new ones, creating simple breakdowns and another couple of features.
## Broken tags finder

Starts the process of finding tracks with damaged tags, breaking and down in a convenient format for further analyze and treatment.

- specify a root music directory
- go over every sub-directory recursively
- for every track find broken tags:
	- title
	- artist
	- album
	- year
	- bitrate
	- album cover
- if the file is healthy - continue
- if at least one of the tags is broken, move the current .mp3 file to a temporal dedicated flat folder called "Broken"
- build a broken.json file with the format:
```JSON
[{
	"name": {"value": "name", "status": "broken"},
	"location": {"value": "absolute path"},
	"title": {"value": "title", "status": "empty/broken"},
	"artist": {"value": "artist", "status": "empty/broken"},
	"album": {"value": "album", "status": "empty/broken"},
	"year": {"value": "year", "status": "empty/broken"},
	"bitrate": {"value": "bitrate", "status": "empty/low"},
	"album cover": {"value": "album cover", "status": "empty"}
}]
```
Note:
- original - initial track's status (tags)
- modified - the new track's status (tags)
- name - current file name
- broken/empty, x - might be either "broken" or "empty", x - is the initial tag value
- the track and the record in the .json are matched by the field "name" in the "original" category
- every tag has its own rules based on which the program determines whether the tag is "broken" or not, with "empty" tags it's straightforward

After this, I will have a complete .json file and containing all tracks that have some flaws. With this list, I can manually of with the help of AI find missing metadata and fill "modified" field with the found information fixing broken tracks.

There are several issues that require manual review after the process:
- some tags can have a value, but it could meaningless or just simply wrong
- bitrate can not be updated, in the case of low bitrate downloading a new version of the song is advised
- album cover can be updated at all. Beyond that, some composition might have a cover, but it might be incorrect. For cover updating AIMP's tag editor can be used, it does the job pretty well

Only after manual review and corrections I can go to the next step.
## Tag modifier

Starts the tag recovery process by assigning new tags to the damaged tracks. This feature uses .json file that it goes over and assigns provided tags to the .mp3 file.

Format of the .json file:
```JSON
[{
	"name": {"value": "name"},
	"location": {"value": "absolute path"},
	"title": {"value": "title", "status": "empty/broken", "modified": "new title"},
	"artist": {"value": "artist", "status": "empty/broken", "modified": "new artist"},
	"album": {"value": "album", "status": "empty/broken", "modified": "new album"},
	"year": {"value": "year", "status": "empty/broken", "modified": "new year"},
	"bitrate": {"value": "bitrate", "status": "empty/low", "modified": "new bitrate"},
	"album cover": {"value": "album cover", "status": "empty"}
}]
```
Note:
- the program will modify only present tags, leaving those that are not specified as they are
- bitrate could not be modifies, so this is just a remark to download a new version of the song
- album cover can't be easily updated, so this filed is missing in the "modified" category

## Sorter

This feature sorts songs and puts them into separated folders along with that it renames files based on their title and artist. Sorter creates new root folder with all the changes.

- the process affect songs only at the first level of hierarchy, assuming that if the folder has sub-folder all the songs in it are already sorted and structured (ready discography)
- complete folders and not musical files are copied as they are
- every song has its "artist" and "album" to determine and create (if needed) its new location
- if a song has several contributing artists (divided by , & or | ) the program takes the first one in order to name a directory
- affected files are auto renamed in the following way: "artist" - "title". In case of several artists they go untouched for the name
### Majority separation

Artist are presented in the playlist in an unequal amounts, based on their presence (how many compositions of this author are there) they are separated to "Остальные" и "Артисты".
To Остальные goes artists below 7 songs and they are not categorized further into separate folder for albums. In "Артисты" are artists who have more presence, and they are categorized by albums, so every song of the singer goes to its corresponding folder.

Every name change and relocation is logged in the following format:
```JSON
[{
	"name": {"value": "name", "modified": "new name"},
	"location": {"value": "absolute path", "modified": "new location"},
}]
```
## Status map builder

Genre breakdown of all the songs.