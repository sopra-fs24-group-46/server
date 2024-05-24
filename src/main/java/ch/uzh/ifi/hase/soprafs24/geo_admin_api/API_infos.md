Max 20 calls per minute on average over one week
---------------------------------------------------------------------------------------
documentation: https://api3.geo.admin.ch/services/sdiservices.html  
Layers: https://api3.geo.admin.ch/api/faq/index.html#which-layers-are-available  
The only layer we'll be using is: ch.swisstopo.swissnames3d  
---------------------------------------------------------------------------------------
The layer swissnmaes3d is th main layer when working with location names like mountain peaks. No specific mountain peak layer exists. The swissname3d layer also constains buildings, lakes, regions... and many more. Without filtering for mountains or lakes this layer can't be used effectively.  
Two main services are provided to ineract with the layers. **Identify** which returns values for a specific layer in a specific region. Some layer support further queries. siwssnames3d is sadly not part of these queryable layers. The other service id **Find** which will search the attributes of enteties on given layer. This way we can retreive enetities of objektart=Gipfel. The response data contains duplicates and cuts off at around 2000 results. Only one attribute at the time can be searched for. This results in incomplete Mountain data.
## By objektart
To filter for Mountains or Lakes use the **find** service 
https://api3.geo.admin.ch/rest/services/api/MapServer/find?layer=ch.swisstopo.swissnames3d&searchText=Gipfel&searchField=objektart
searchField=objektart
searchText={Gipfel|Alpiner Gipfel|Huegel|Haupthuegel|see} //only use one of them gifpel will include alpiner gipfel

Further filtering has to be done locally (in java)

## By Region
To get featurs from a region use the **identify** service //problem here is we get many buildings and so on. Probbably need
several API calls to get all the mountains to later filter them (LayerDefs can't be applied for swissnames3d).
https://api3.geo.admin.ch/rest/services/api/MapServer/identify?layers=all:ch.swisstopo.swissnames3d&geometryType=esriGeometryPolygon&tolerance=0&geometry={%22rings%22:[[[675000,245000],[670000,255000],[680000,260000],[690000,255000],[685000,240000],[675000,245000]]]}
geometry={"rings":[[675000,245000],[670000,255000],[680000,260000],[690000,255000],[685000,240000],[675000,245000]]}
geometryType=esriGeometryPolygon
tolerance=0
offset=2 //to get the next 200 features


this returns all the names in the ch.swisstopo.swissnames3d layer including buidlings and so on

Further filtering has to be done locally (in java)

---------------------------------------------------------------------------------------
Following are some olde notes these are probbably useless:
---------------------------------------------------------------------------------------
This is a call looking for the word Bern in the names of all Cantonal boundaries

https://api3.geo.admin.ch/rest/services/api/MapServer/find?layer=ch.swisstopo.swissboundaries3d-kanton-flaeche.fill&searchText=Bern&searchField=name

Deconstructed into:
url:        https://api3.geo.admin.ch/rest/services/api/MapServer/
function:   find?
Params:
layer:      layer=ch.swisstopo.swissboundaries3d-kanton-flaeche.fill
query:      searchText=Bern
field:      searchField=name
---------------------------------------------------------------------------------------
This is looking for common names in the defined Polygon. Returns max 50 features, to get next page add the necessary
page number

https://api3.geo.admin.ch/rest/services/api/MapServer/identify?geometry={%22rings%22:[[[675000,245000],[670000,255000],[680000,260000],[690000,255000],[685000,240000],[675000,245000]]]}&geometryType=esriGeometryPolygon&imageDisplay=500,600,96&mapExtent=548945.5,147956,549402,148103.5&tolerance=5&layers=all:ch.swisstopo.vec200-names-namedlocation

https://api3.geo.admin.ch/rest/services/api/MapServer/identify?layers=all:ch.swisstopo.swissnames3d&geometryType=esriGeometryPoint




---------------------------------------------------------------------------------------
get infos about layer https://api3.geo.admin.ch/rest/services/api/MapServer/{layerBodId}
important layers:

ch.swisstopo.swissnames3d
    Containing names with useful additional information

----------------------------------------------------------------------------------------
ch.swisstopo.vec200-names-namedlocation 
    Containing names of locations like peaks in the 1:200'000 scale probably too minimalistic

ch.swisstopo.swissboundaries3d-kanton-flaeche.fill
    Containing the boundaries of the cantons

ch.bafu.vec25-seen
    Containing info about lakes

ch.swisstopo.vec25-primaerflaechen
    Containing info about surface lake forest or ...
