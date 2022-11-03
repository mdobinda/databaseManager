# The client user execution script for Project Three - CNT 4714 - Fall 2022
# all commands assumed to be executed by the client user
# the client user has only selection  privilege on the project3 database schema
#
#Command 1:
#   Query: Which rider won the World Championship - Elite Women in 2021?
select ridername
from racewinners
where racename = 'World Championship - Elite Women' and raceyear = 2021;

#Commands 2A, 2B, and 2C:
#   Delete all the riders from Norway from the riders table.
#   * * * Do a "before" and "after" select * from riders for this command.
#   Note: the before and after select statements will execute, but the delete will not
#         thus no changes will be reflected in the before and after snapshots.
select * from riders;
delete from riders where nationality = 'Norway';
select * from riders;

#Commands 3A, 3B, and 3C:
#    Update rider Marianne Vos to show number of wins = 245 in the riders table.
# * * Do a "before" and "after" selection on the riders table
#   Note: the before and after select statements will execute, but the delete will not
#         thus no changes will be reflected in the before and after snapshots.
select * from riders;
update riders set num_pro_wins = 245 where ridername = "Marianne Vos";
select * from riders;

#Command 4:
#   Query: Which rider won the 2021 Tour de France?
select ridername
from racewinners
where racename = "Tour de France" and raceyear = 2021;

#Command 5:
#   How many riders are there?
select count(ridername) as number_of_riders
from riders;

#Command 6:
#   udpating command not valid for the client user
update teams
set registered_nation = "France"
where teamname = "Movistar";