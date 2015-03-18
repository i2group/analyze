Notes on this example

In httpd.conf: A rule for the URL only rewrites, and a rule for the servlet rewrites.
(These entries need to be above the generated entry for download, otherwise they won't be hit, since the rules are processed in order.
i.e. rules need to go from most constrained to least constrained in terms of their patterns)

RewriteCond %{QUERY_STRING} ^documentId=DA-documents/([^&]+)
RewriteRule ^/apollo/services/download /<DSID>/documents/%1? [P]

RewriteCond %{QUERY_STRING} ^documentId=DA-servlet/([^&]+)
RewriteRule ^/apollo/services/download /<DSID>/services/dadownload?documentId=%1 [P]

in plugin-cfg.xml for the URL only rule to host documents add:
<Uri AffinityCookie="JSESSIONID" AffinityURLIdentifier="jsessionid" Name="/<DSID>/documents/*"/>
The rule for the servlet should be present already:
<Uri AffinityCookie="JSESSIONID" AffinityURLIdentifier="jsessionid" Name="/<DSID>/services/dadownload/*"/>

Substitute <DSID> with the appropriate appropriate data source id when the data source is added to the file.

The rewrite rules in httpd capture requests that have come specifically from DA with the "DA-documents" or "DA-servlet" prefix.
DA-documents is then redirected to the original url after the DA-documents/ prefix
so this changes

.../apollo/services/download?documentId=DA-documents/picture3.jpg
to just /<DSID>/documents/picture3.jpg

NB this example will only work with relative urls

The DA-servlet rule extracts the value after DA-servlet/ and sends it to the External Data File Download Servlet.
so this changes

.../apollo/services/download?documentId=DA-servlet/picture3.jpg
to /<DSID>/services/dadownload?documentId=picture3.jpg


To get example to work, do same as for daod-subset-filesystem-example except using da-subset-documents-example for the data source name.
Find the data source id created from dsid folder

create the above entries in httpserver files with data source id you found and restart httpserver

data1.xml, data2.xml changed to include documentUrl and name
xslt changed to map that data to correct xml
documents folder created containing pictures (which are images already distributed with iBase)

