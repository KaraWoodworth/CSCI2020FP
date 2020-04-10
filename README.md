# CSCI2020FP
Final Project for CSCI2020

First run Server then Client

how to use program:
Once data is loaded, user can click on bar segments in the bar graph.
The green section represents courses flagged as 'marked'
The blue and grey section represents course items not yet evaluated'
The blue is a projected final mark based on completed work. So if you
got 60% on everything, if you continue this trend, then the blue is what
your final could be, while the grey is any remaining grade that would require
getting 100% to achieve the top of it.

A breakdown of the grades that make up a bar segment can be found if you click
on a bar segment.

Course related work:
Uses client-server. Client gets data from server then runs a graph
Database: server reads csv and turns it into data for the client
threading: server runs on a thread that monitors when a client connects.
client runs a thread when recieving data from server
utilizes custom shapes, extends Rectangle