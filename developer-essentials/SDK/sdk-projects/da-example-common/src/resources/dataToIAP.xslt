<?xml version="1.0" encoding="UTF-8"?>
<!--
Copyright (c) 2014 IBM Corp.
All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html

Contributors:
   IBM Corp - initial API and implementation and initial documentation
-->
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" encoding="UTF-8" indent="yes" />
    <xsl:param name="filename" />

    <xsl:template match="Data">
        <QueryResult>
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
            <_1_ItemId>
                <xsl:value-of select="@id" />
            </_1_ItemId>
            <Person_FullName>
                <xsl:apply-templates select="FirstName" />
                <xsl:apply-templates select="LastName" />
            </Person_FullName>
            <xsl:apply-templates select="DOB" />
            <xsl:call-template name="itemTemplate" />
        </Person>
    </xsl:template>

    <xsl:template match="Associations">
        <Associates>
            <xsl:apply-templates select="Association"/>
        </Associates>
    </xsl:template>

    <xsl:template match="Association ">
        <Associate>
            <_1_ItemId>
                <xsl:value-of select="@id" />
            </_1_ItemId>
            <xsl:apply-templates select="Actor1" />
            <xsl:apply-templates select="Actor2" />
            <_4_LinkDirection>NONE</_4_LinkDirection>
            <_5_LinkStrength>Confirmed</_5_LinkStrength>
            <xsl:call-template name="itemTemplate" />
        </Associate>
    </xsl:template>

    <xsl:template name="itemTemplate">
        <_6_Provenance>
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
            <RetrievalBlocks/>
        </_6_Provenance>
        <_7_SecurityTagIds>
            <SecurityTagId>d3cdf9a0-1164-11e3-8ffd-0800200c9a66</SecurityTagId>
            <SecurityTagId>6f0a69d4-6edd-40ec-a372-c6db33262a58</SecurityTagId>
        </_7_SecurityTagIds>
        <_8_PropertyBags>
            <PropertyBag>
                <DateTimes>
                    <xsl:apply-templates select="RecordCreated" />
                    <xsl:apply-templates select="RecordModified" />
                </DateTimes>
            </PropertyBag>
        </_8_PropertyBags>
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
        <_2_FromEndId>
            <xsl:value-of select="."></xsl:value-of>
        </_2_FromEndId>
    </xsl:template>

    <xsl:template match="Actor2">
        <_3_ToEndId>
            <xsl:value-of select="."></xsl:value-of>
        </_3_ToEndId>
    </xsl:template>

    <xsl:template match="SecurityTag">
        <SecurityTagId>
            <xsl:value-of select="."></xsl:value-of>
        </SecurityTagId>
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
</xsl:stylesheet>
