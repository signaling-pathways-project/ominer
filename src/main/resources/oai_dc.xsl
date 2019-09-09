<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        version="1.0">
    <xsl:output omit-xml-declaration="yes" method="xml" indent="yes" />

    <xsl:template match="/">
        <oai_dc:dc  xmlns:dc="http://purl.org/dc/elements/1.1/"
                xmlns:oai_dc="http://www.openarchives.org/oai/2.0/oai_dc/"
                   xmlns:xsi="http://www.w3.org/2001/xmlschema-instance"
                   xsi:schemalocation="http://www.openarchives.org/oai/2.0/oai_dc/ http://www.openarchives.org/oai/2.0/oai_dc.xsd">


            <xsl:for-each select="//*[local-name()='element'][@name ='title']">
                <dc:title>
                    <xsl:value-of select="."/>
                </dc:title>
            </xsl:for-each>

            <xsl:for-each select="//*[local-name()='element'][@name ='creator']">
                <dc:creator>
                    <xsl:value-of select="."/>
                </dc:creator>
            </xsl:for-each>

            <xsl:for-each select="//*[local-name()='element'][@name ='subject']">
                <dc:subject>
                    <xsl:value-of select="."/>
                </dc:subject>
            </xsl:for-each>

            <xsl:for-each select="//*[local-name()='element'][@name ='description']">
                <dc:description>
                    <xsl:value-of select="."/>
                </dc:description>
            </xsl:for-each>

            <xsl:for-each select="//*[local-name()='element'][@name ='publisher']">
                <dc:publisher>
                    <xsl:value-of select="."/>
                </dc:publisher>
            </xsl:for-each>

            <xsl:for-each select="//*[local-name()='element'][@name ='contributor']">
                <dc:contributor>
                    <xsl:value-of select="."/>
                </dc:contributor>
            </xsl:for-each>

            <xsl:for-each select="//*[local-name()='element'][@name ='date']">
                <dc:date>
                    <xsl:value-of select="."/>
                </dc:date>
            </xsl:for-each>

            <xsl:for-each select="//*[local-name()='element'][@name ='type']">
                <dc:type>
                    <xsl:value-of select="."/>
                </dc:type>
            </xsl:for-each>

            <xsl:for-each select="//*[local-name()='element'][@name ='format']">
                <dc:format>
                    <xsl:value-of select="."/>
                </dc:format>
            </xsl:for-each>

            <xsl:for-each select="//*[local-name()='element'][@name ='identifier']">
                <dc:identifier>
                    <xsl:value-of select="."/>
                </dc:identifier>
            </xsl:for-each>

            <xsl:for-each select="//*[local-name()='element'][@name ='source']">
                <dc:source>
                    <xsl:value-of select="."/>
                </dc:source>
            </xsl:for-each>

            <xsl:for-each select="//*[local-name()='element'][@name ='language']">
                <dc:language>
                    <xsl:value-of select="."/>
                </dc:language>
            </xsl:for-each>

            <xsl:for-each select="//*[local-name()='element'][@name ='relation']">
                <dc:relation>
                    <xsl:value-of select="."/>
                </dc:relation>
            </xsl:for-each>

            <xsl:for-each select="//*[local-name()='element'][@name ='coverage']">
                <dc:coverage>
                    <xsl:value-of select="."/>
                </dc:coverage>
            </xsl:for-each>

            <xsl:for-each select="//*[local-name()='element'][@name ='rights']">
                <dc:rights>
                    <xsl:value-of select="."/>
                </dc:rights>
            </xsl:for-each>


        </oai_dc:dc>
    </xsl:template>

</xsl:stylesheet>