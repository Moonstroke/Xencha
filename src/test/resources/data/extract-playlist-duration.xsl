<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fn="http://www.example.com/xslt-functions">
	<xsl:template match="*">
		<xsl:copy>
			<xsl:apply-templates/>
		</xsl:copy>
	</xsl:template>

	<xsl:template match="tracklist">
		<totalDuration>
			<xsl:value-of select="fn:seconds-to-time(sum(track/duration/fn:time-to-seconds(.)))"/>
		</totalDuration>
	</xsl:template>

	<xsl:function name="fn:seconds-to-time">
		<xsl:param name="seconds"/>
		<xsl:value-of select="$seconds div 60"/>
		<xsl:text>:</xsl:text>
		<xsl:value-of select="$seconds mod 60"/>
	</xsl:function>

	<xsl:function name="fn:time-to-seconds">
		<xsl:param name="time"/>
		<xsl:variable name="minutes" select="number(substring-before($time, ':'))"/>
		<xsl:variable name="seconds" select="number(substring-after($time, ':'))"/>
		<xsl:value-of select="$minutes * 60 + $seconds"/>
	</xsl:function>
</xsl:stylesheet>
