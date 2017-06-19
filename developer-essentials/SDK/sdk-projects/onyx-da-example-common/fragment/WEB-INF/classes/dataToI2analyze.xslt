<?xml version="1.0" encoding="UTF-8"?>
<!--
Copyright (c) 2014, 2017 IBM Corp.
All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html

Contributors:
   IBM Corp - initial API and implementation and initial documentation
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:tns="http://www.i2group.com/Schemas/2013-10-03/ModelData/Provenance">
    <xsl:output method="xml" encoding="UTF-8" indent="yes" />
    <xsl:param name="filename" />

    <xsl:template match="Data">
        <QueryResult xmlns:tns="http://www.i2group.com/Schemas/2013-10-03/ModelData/Provenance">
            <xsl:apply-templates select="Actors"/>
            <xsl:apply-templates select="Associations"/>
        </QueryResult>
    </xsl:template>

    <xsl:template match="Actors">
        <Persons>
            <xsl:apply-templates select="Actor"/>
        </Persons>
    </xsl:template>

    <xsl:template match="Actor">
        <Person>
            <xsl:call-template name="itemTemplate" />
            <Cards>
                <Card>
                    <xsl:call-template name="cardProvenanceTemplate" />
                    <xsl:apply-templates select="DOB" />
                    <Person_FullName>
                        <xsl:apply-templates select="LastName" />
                        <xsl:apply-templates select="FirstName" />
                    </Person_FullName>
                  <Person_Imagery>
                            <Person_Image>
                                     <xsl:apply-templates select="DocumentUrl" />
                                     <xsl:apply-templates select="DocumentName" />
                            </Person_Image>
                  </Person_Imagery>
                </Card>
            </Cards>
        </Person>
    </xsl:template>

    <xsl:template match="Associations">
        <Associates>
            <xsl:apply-templates select="Association"/>
        </Associates>
    </xsl:template>

    <xsl:template match="Association ">
        <Associate>
            <xsl:call-template name="itemTemplate" />
            <xsl:apply-templates select="Actor1" />
            <xsl:apply-templates select="Actor2" />
            <LinkDirection>NONE</LinkDirection>
            <LinkStrength>Confirmed</LinkStrength>
            <Cards>
                <Card>
                    <xsl:call-template name="cardProvenanceTemplate" />
                </Card>
            </Cards>
        </Associate>
    </xsl:template>

    <xsl:template name="itemTemplate">
        <ItemId>
            <xsl:value-of select="@id" />
        </ItemId>
        <ItemVersion>0</ItemVersion>
        <SecurityTagIds>
            <SecurityTagId>UC</SecurityTagId>
            <SecurityTagId>OSI</SecurityTagId>
        </SecurityTagIds>
        <PropertyBags>
            <PropertyBag>
                <DateTimes>
                    <xsl:apply-templates select="RecordCreated" />
                    <xsl:apply-templates select="RecordModified" />
                </DateTimes>
            </PropertyBag>
        </PropertyBags>
    </xsl:template>

    <xsl:template name="cardProvenanceTemplate">
        <tns:CardProvenance>
            <RetrievalBlocks/>
            <OriginId>
                <xsl:attribute name="Type">IAP.SDK.Example</xsl:attribute>
                <xsl:attribute name="Version">1</xsl:attribute>
                <Key>
                    <String>
                        <xsl:value-of select="$filename" />
                    </String>
                    <String>
                        <xsl:value-of select="@id" />
                    </String>
                </Key>
            </OriginId>
        </tns:CardProvenance>
    </xsl:template>

    <xsl:template match="FirstName">
        <Person_First_Given_Name>
            <xsl:value-of select="."></xsl:value-of>
        </Person_First_Given_Name>
    </xsl:template>

    <xsl:template match="LastName">
        <Person_FamilyName>
            <xsl:value-of select="."></xsl:value-of>
        </Person_FamilyName>
    </xsl:template>

    <xsl:template match="DOB">
        <Person_DateofBirth>
            <xsl:value-of select="."></xsl:value-of>
        </Person_DateofBirth>
    </xsl:template>

    <xsl:template match="Actor1">
        <LinkFromEndId>
            <xsl:value-of select="."></xsl:value-of>
        </LinkFromEndId>
    </xsl:template>

    <xsl:template match="Actor2">
        <LinkToEndId>
            <xsl:value-of select="."></xsl:value-of>
        </LinkToEndId>
    </xsl:template>

    <xsl:template match="RecordCreated">
        <DateTime>
            <xsl:value-of select="."></xsl:value-of>
        </DateTime>
    </xsl:template>

    <xsl:template match="RecordModified">
        <DateTime>
            <xsl:value-of select="."></xsl:value-of>
        </DateTime>
    </xsl:template>

    <xsl:template match="DocumentUrl">
         <DocumentId>
             <xsl:value-of select="."></xsl:value-of>
         </DocumentId>
    </xsl:template>

    <xsl:template match="DocumentName">
        <Name>
              <xsl:value-of select="."></xsl:value-of>
        </Name>
    </xsl:template>


</xsl:stylesheet>
