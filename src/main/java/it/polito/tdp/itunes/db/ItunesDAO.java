package it.polito.tdp.itunes.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.itunes.model.Adiacenza;
import it.polito.tdp.itunes.model.Album;
import it.polito.tdp.itunes.model.Artist;
import it.polito.tdp.itunes.model.Genre;
import it.polito.tdp.itunes.model.MediaType;
import it.polito.tdp.itunes.model.Playlist;
import it.polito.tdp.itunes.model.Track;

public class ItunesDAO {
	
	public List<Album> getAllAlbums(){
		final String sql = "SELECT * FROM Album";
		List<Album> result = new LinkedList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				result.add(new Album(res.getInt("AlbumId"), res.getString("Title")));
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}
		return result;
	}
	
	public List<Artist> getAllArtists(){
		final String sql = "SELECT * FROM Artist";
		List<Artist> result = new LinkedList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				result.add(new Artist(res.getInt("ArtistId"), res.getString("Name")));
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}
		return result;
	}
	
	public List<Playlist> getAllPlaylists(){
		final String sql = "SELECT * FROM Playlist";
		List<Playlist> result = new LinkedList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				result.add(new Playlist(res.getInt("PlaylistId"), res.getString("Name")));
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}
		return result;
	}
	
	public void getAllTracks(Map<Integer, Track> idMap){
		final String sql = "SELECT * FROM Track";
		
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				
				if(!idMap.containsKey(res.getInt("TrackId"))) {	//Aggiungo la track solo se non già presente.
					
				idMap.put(res.getInt("TrackId"), new Track(res.getInt("TrackId"), res.getString("Name"), 
						res.getString("Composer"), res.getInt("Milliseconds"), 
						res.getInt("Bytes"),res.getDouble("UnitPrice")));
				}
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}
	}
	
	//Ho già il metodo che mi recupera tutti i generi. Posso recuperarli nel modello.
	public List<Genre> getAllGenres(){
		final String sql = "SELECT * FROM Genre";
		List<Genre> result = new LinkedList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				result.add(new Genre(res.getInt("GenreId"), res.getString("Name")));
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}
		return result;
	}
	
	public List<MediaType> getAllMediaTypes(){
		final String sql = "SELECT * FROM MediaType";
		List<MediaType> result = new LinkedList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				result.add(new MediaType(res.getInt("MediaTypeId"), res.getString("Name")));
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}
		return result;
	}

	
	public List<Track> getVertici(Genre g, Map<Integer, Track> idMap){
		
		String sql = "SELECT TrackId "
				+ "FROM track "
				+ "WHERE GenreId = ?";
		
		List<Track> result = new ArrayList<Track>();
		
		try {
			
			Connection conn = DBConnect.getConnection();
			
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, g.getGenreId());
			
			ResultSet res = st.executeQuery();
			
			while(res.next()) {
				
				result.add(idMap.get(res.getInt("TrackId")));	//Mi conviene passare anche la mappa come parametro per semplificarmi la vita. Così posso aggiungere l'oggetto recuperandolo dalla mappa passandogli l'id della canzone.
			}
			conn.close();
			return result;
			
		}catch(SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Adiacenza> getArchi(Genre g, Map<Integer, Track> idMap){
		
		String sql=  "SELECT t1.TrackId AS v1, t2.TrackId AS v2, abs(t1.Milliseconds-t2.Milliseconds) AS delta "	//Il grafo è non orientato quindi il peso devo prenderlo sempre in valore assoluto perché deve essere positivo. Ricorda: peso = differenza di durata in millisecondi.
				+ "FROM track t1, track t2 "
				+ "WHERE t1.MediaTypeId = t2.MediaTypeId AND t1.TrackId > t2.TrackId AND t1.GenreId = ? AND t1.GenreId = t2.GenreId";	//Ricordarsi assolutamente di inserire anche la condizione sul genere nella query degli archi perché la query deve essere "collegata" a quella per i vertici. Se il genere non è lo stesso potrei aggiungere archi con vertici non presenti nel grafo, ovvero fare casino.
		
		List<Adiacenza> result = new ArrayList<Adiacenza>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			
			st.setInt(1, g.getGenreId());
			
			ResultSet res = st.executeQuery();
			
			while(res.next()) {
				
				result.add(new Adiacenza(idMap.get(res.getInt("v1")), idMap.get(res.getInt("v2")), res.getInt("delta")));
			}
			conn.close();
			return result;
			
		}catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
	}
} 
