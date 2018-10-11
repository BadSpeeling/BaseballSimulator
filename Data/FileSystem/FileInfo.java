package FileSystem;

public class FileInfo {
	
	public static FileMetaData players = new FileMetaData ("players.txt", "PlayerID,TeamID,LeagueID,FirstName,LastName,Position,Speed"); 
	public static FileMetaData playersBatting = new FileMetaData ("playersBatting.txt", "ID,Contact,Power,Discipline,HitBallType,SprayType");
	public static FileMetaData playersPitching = new FileMetaData ("playersPitching.txt", "ID,FBVelo,FBMovement,FBControl");
	public static FileMetaData playersSeasonBattingStats = new FileMetaData ("playersSeasonBatting.txt", "ID,Year,TeamID,LeagueID,GS,G,PA,AB,Hits,Doubles,Triples,Homeruns,Walks,Strikeouts,RunsBattedIn,RunsScored");
	public static FileMetaData playerSeasonPitchingStats = new FileMetaData ("playersSeasonPitching.txt", "ID,Year,TeamID,LeagueID,GS,G,IP,Hits,Doubles,Triples,Homeruns,Walks,Strikeouts,RunsAllowed");
	
}
