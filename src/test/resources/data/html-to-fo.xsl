<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<xsl:template match="/">
		<fo:root>
			<fo:layout-master-set>
				<fo:simple-page-master master-name="a4" page-height="29.7cm" page-width="21cm">
					<fo:region-body region-name="xsl-region-body" margin="25mm"/>
				</fo:simple-page-master>
			</fo:layout-master-set>
			<xsl:apply-templates/>
		</fo:root>
	</xsl:template>

	<xsl:template match="html">
		<fo:page-sequence master-name="a4">
			<fo:static-content flow-name="xsl-region-before">
				<xsl:apply-templates match="head/title"/>
			</fo:static-content>
			<xsl:apply-templates match="body"/>
		</fo:page-sequence>
	</xsl:template>

	<xsl:template match="title">
		<fo:block text-align="center">
			<xsl:value-of select="text()"/>
		</fo:block>
	</xsl:template>

	<xsl:template match="h1">
		<fo:block font-weight="bold" text-decoration="underline" font-size="24pt">
			<xsl:value-of select="text()"/>
		</fo:block>
	</xsl:template>

	<xsl:template match="p">
		<fo:block>
			<xsl:value-of select="text()"/>
		</fo:block>
	</xsl:template>
</xsl:stylesheet>
