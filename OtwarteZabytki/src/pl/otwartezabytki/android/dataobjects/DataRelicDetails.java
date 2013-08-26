package pl.otwartezabytki.android.dataobjects;

import android.os.Parcel;
import android.os.Parcelable;

public class DataRelicDetails implements Parcelable{

	long id;
	int relic_id;
	int android_id;
	private String mainPhotoURL="";
	private String mainPhotoPath="";
	private String identification="";
	private String description="";
	private String state="";
	private String dating="";
	private String street="";
	private String place="";
	private String commune="";
	private String district="";
	private String voivodeship="";
	private String longitude="";
	private String latitude="";
	private String opinion="";
	
	public DataRelicDetails(long id, int relic_id, int android_id, String main_photo, String mainPhotoPath, String identification, String description, String state,
			String dating, String street, String place, String commune, String district, String voivodeship, String longitude, String latitude, String opinion){
		this.id = id;
		this.relic_id = relic_id;
		this.android_id = android_id;
		this.mainPhotoURL = main_photo;
		this.mainPhotoPath = mainPhotoPath;
		this.identification = identification;
		this.description = description;
		this.state = state;
		this.dating = dating;
		this.street = street;
		this.place = place;
		this.commune = commune;
		this.district = district;
		this.voivodeship = voivodeship;
		this.longitude = longitude;
		this.latitude = latitude;
		this.opinion = opinion;
	}
	
	public DataRelicDetails(){
		
	}
	
	public DataRelicDetails(Parcel source){
		id = source.readLong();
		relic_id = source.readInt();
		android_id = source.readInt();
		mainPhotoURL = source.readString();
		mainPhotoPath = source.readString();
		identification = source.readString();
		description = source.readString();
		state = source.readString();
		dating = source.readString();
		street = source.readString();
		place = source.readString();
		commune = source.readString();
		district = source.readString();
		voivodeship = source.readString();
		longitude = source.readString();
		latitude = source.readString();
		opinion = source.readString();
	}
	
	
	public int getRelic_id() {
		return relic_id;
	}

	public void setRelic_id(int relic_id) {
		this.relic_id = relic_id;
	}


	public String getIdentification() {
		return identification;
	}

	public void setIdentification(String identification) {
		this.identification = identification;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getDating() {
		return dating;
	}

	public void setDating(String dating) {
		this.dating = dating;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getCommune() {
		return commune;
	}

	public void setCommune(String commune) {
		this.commune = commune;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getVoivodeship() {
		return voivodeship;
	}

	public void setVoivodeship(String voivodeship) {
		this.voivodeship = voivodeship;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public int getAndroid_id() {
		return android_id;
	}

	public void setAndroid_id(int android_id) {
		this.android_id = android_id;
	}

	public String getMainPhotoPath() {
		return mainPhotoPath;
	}

	public void setMainPhotoPath(String mainPhotoPath) {
		this.mainPhotoPath = mainPhotoPath;
	}

	public String getMainPhotoURL() {
		return mainPhotoURL;
	}

	public void setMainPhotoURL(String mainPhotoURL) {
		this.mainPhotoURL = mainPhotoURL;
	}

	public String getOpinion() {
		return opinion;
	}

	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}

	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel destination, int flags) {
		destination.writeLong(id);
		destination.writeInt(relic_id);
		destination.writeInt(android_id);
		destination.writeString(mainPhotoURL);
		destination.writeString(mainPhotoPath);
		destination.writeString(identification);
		destination.writeString(description);
		destination.writeString(state);
		destination.writeString(dating);
		destination.writeString(street);
		destination.writeString(place);
		destination.writeString(commune);
		destination.writeString(district);
		destination.writeString(voivodeship);
		destination.writeString(longitude);
		destination.writeString(latitude);
		destination.writeString(opinion);
	}
	
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public DataRelicDetails createFromParcel(Parcel source) {
            return new DataRelicDetails(source); 
        }

        public DataRelicDetails[] newArray(int size) {
            return new DataRelicDetails[size];
        }
    };
}
