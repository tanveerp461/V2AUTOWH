@echo off
CD C:\V2AutoWH
ant deleteoldreportsandlogs createmasterxlsx createtestngxml createtestcases clean compile run makexsltreports emailreports createdashboard
PAUSE