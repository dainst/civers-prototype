# Technical documentation

## Service architecture

Services:

- Scraper
- Citator
    - provides Widget
- DOI Registrar
- Widget Host
    - embeds Widget

The Citator and the DOI Registrar each have their own database to
keep track of registered resources (DOI, URL, storage paths, dates, etc.).

Additionally there is one file storage, which is shared amongst the 
Scraper and the Citator.

## Webscraping

TODO provide more info here
