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
button which triggers the archival of the page it is embedded in, by sending a request for archival to the *Citator*. In the context of the prototype the **Widget Host** represents an exemplary external website.

Note that the webscraping unit of the current prototype is limited in its capabilities (see [technical documentation](./README_TECHNICAL.md#webscraping))
and as a proof of concept optimized to archive entity pages of [arachne.dainst.org](https://arachne.dainst.org), for example 
[arachne.dainst.org/entity/1215966](https://arachne.dainst.org/entity/1215966). 
To get representative results use one of those when following along the described usage scenarios.

## Use cases

There are two basic use cases of the overall system. 

1. There is a person **writing** a paper or an article who wants to **cite** a digital resource.
2. There is a person **reading** a paper or an article who encounters a citation in a footnote and wants to **look up** the cited digital resource.

In the first use case the writer's goal is to obtain a stable identifier, pointing to a permanently stable
resource in the internet. To get one, he starts by either visiting the *Citator* directly, entering the URL of the resource to cite, or by visiting the resource to cite, provided it embeds the *Citator Widget*. In either case he then requests a DOI which he can use for citation. The system responds with such a DOI.

In the second use case we put ourselves in the shoes of the reader. With a citation in hand he visits the *DOI Registrar* to find the permanently accessible representation of the cited resource. He enters the resource's DOI and gets redirected to an overview page. This page describes the resource and provides links to a screenshot, a snapshot of the resource of the site taken at the time of citation. It also provides links to older and newer versions of the same site, taken at other points in time.

Here are the steps to follow along in the prototype:

##### Obtaining a DOI

To obtain a DOI directly via the *Citator*, do the following:

1. Visit Citator
2. Insert URL 
3. Click "submit"
4. Wait a couple of seconds 
5. The archived resource should appear as the topmost item under "Resources"

The DOI of the archived resource shows up as a link in a listing of all archived resources (showing the newest at the top), providing access to the detail page of the archived resource.

To obtain the DOI via the *Citation Widget*, do the following:

1. Visit the Widget Host
1. In the Citation Widget for a given resource, click "submit"
1. After a copule of seconds, the widget responds with a DOI for citation 

##### Looking up a DOI

Given a DOI acquired by following along the previously described steps, do the following
to look up the archived resource:

1. Visit the DOI Registrar
1. Enter the DOI
1. You will get redirected to the resource

The DOI of the archived resource also shows up as a link in a listing of all registered resources (showing the newest at the top), providing access to the detail page of the archived resource.

Note that all archived resources get listed in the *Citator* as well as in the *DOI Registrar*, newest first.

Note also that the process could be streamlined even further, by letting users cite not only a DOI,
but a URL including a DOI. This would look something like this: `http://localhost:8021/doi/123456` (where `localhost:8021` would be the URL of some DOI registry). Entering this URL into the browser
would directly redirect to the archived page hosted under the domain of the *Citator* it was archived with. It should be emphasized that we do not want to cite the archived resource directly, i.e. we do not want to cite the URL for the archived resource itself. The reason for this is that there can be multiple *Citators*, hosted at different institutions. The user should not know about this but instead always go the route via the (or "a") *DOI Registrar*.

### Citing a resource again

Now we assume now there is another writer who also wants to cite the exact same resource, at a later point in time. Of course he will follow the exact same steps to obtain a citable DOI.

Let's see how this works in the prototype. Once you finished a first walkthrough through a complete use case (creating a snapshot, looking up the resource with the help of the generated DOI), visit the *Widget Host* again and reload the page. Now, when you press the "submit" button of the *Widget* again, it returns with the exact same DOI it already returned on the first time. Why is that?

To save resources, the *Citator* compares the first snapshot of the site with the one taken earlier. To do this, it compares the two snapshots by "Last updated" date. If it is the same, which it is in this case, it will simply return an existing DOI.

To simulate the case where the page got updated in the meanwhile, the resource detail pages of the *Widget Host* provides a button which let's one change the last updated date of the resource. If this is different (really, greater than, but in the prototype we simply can choose between two dates) then a new snapshot gets taken, a new version of the archived resource created, and a new DOI gets returned to the user.
