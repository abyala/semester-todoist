# Semester Todoist

## Purpose

I am using this little utility to easily convert my professors' assignment PDFs into a properly formatted CSV to 
import into [Todoist](https://todoist.com).

## Usage

I'm just running this from within my IDE using the REPL, but the key function in the `core` namespace is
`(execute input-file output-file)` where the `input-file` is the source EDN file and the `output-file` is the
target CSV file.

## Input file format

```clojure
{:id         2038
 :name       "Abnormal Psychology"
 :start-date "2024-08-28"
 :off-weeks  ["2024-09-11" "2024-09-18"]
 :weeks      [
              ["Course Introduction"
               [[:read "Some textbook, Chapter 1"]
                [:read "Some textbook, Chapter 2"]
                [:watch "A video" "https://examples.com/video-url"]]]
              ["First Main Lecture"
               [[:read "Some textbook, Chapter 3"]
                [:project "Initial project topic submission"]]]]}
```

The `start-date` is the date when all tasks for the first week should show up. I want them to appear the day after the
last classes of the previous week, so that the "start" of my week is when I assign out my tasks for the next seven days.
Sometimes there are off-weeks, like for spring break, when we don't have anything due, so the `:off-weeks` vector is an
optional collection days (weeks off of `:start-date`) to ignore; in this example, the third week would be on 
`2024-09-11`, but that and the following weeks we have off (I wish), so week 3 would automatically show up as
`2024-09-25`.

## Output file format

The output is a CSV, based on
[Todoist's specifications](https://todoist.com/help/articles/format-a-csv-file-to-import-into-todoist-UVUXTmm6) with
the following headings and values that I use:

* TYPE: Always "task"
* CONTENT: The name of the task, possibly preceded by an emoji, like 📖 for a reading assignment or 🎥 for a video to
watch.
* DESCRIPTION: The optional description of a task. This is the optional third argument of each assignment from the
input file (the video URL above).
* PRIORITY: Because I don't "complete" tasks for each week, those are p4, as are all optional assignments. Otherwise,
they are p2 for `:project` tasks, and p3 for the rest.
* INDENT: The tasks for each week are set to indent 1, and all assignments are at level 2 underneath.
* AUTHOR: Always blank
* RESPONSIBLE: Always blank
* DATE: The due date, based on the start date and week number. Only "week" tasks have no due date.
* DATE_LANG: Always `"en"` if there is a `DATE` value.
* TIMEZONE: Always blank
* DURATION: Always blank
* DURATION_UNIT: Always blank
