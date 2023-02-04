# Documentation

The prototype demonstrates the interaction of multiple services, which under
realistic circumstances would be hosted independently at different places.

The main component - the system to be designed - is the **Citator**, the component
generating the DOIs and capable of taking snapshots of 
and providing access to the archived websites.

The **DOI Registrar** emulates a central repository where DOIs from different sources
can be registered and searched. From here one can navigate to the sites registered by the *Citator*.

Although the *Citator* works standalone, that is, one can enter URLs to archive via its user interface, 
it also provides a **Widget**, which external websites can embed. The widget provides a 
button which triggers the archival of the page it is embedded in, by sending a request for archival to the *Citator*. 
In the context of the prototype the **Widget Host** represents an exemplary external website.

## Use cases

Note that the webscraping unit of the current prototype is limited in its capabilities (see [technical documentation](./README_TECHNICAL.md))
and as a proof of concept optimized to archive entity pages of [arachne.dainst.org](https://arachne.dainst.org), for example 
[arachne.dainst.org/entity/1215966](https://arachne.dainst.org/entity/1215966). 
To get representative results use one of those in the following use cases.

### Use case 1 - Archive a site via Citator

1. Visit Citator
2. Insert URL 
3. Click "submit"
4. Wait a couple of seconds 
5. The archived resource should appear as the topmost item under "Resources"
6. Click its generated DOI to access the detail view for the newly created resources
7. Follow any of the links
8. Go back
9. Visit DOI Registrar
10. The archived resource should appear there as the topmost item 

### Use case 2 - Archive a site via Widget Host

1. Visit Widget host
2. Click "submit"
3. Visit Citator
4. Continue with step 4 of Use case 1
