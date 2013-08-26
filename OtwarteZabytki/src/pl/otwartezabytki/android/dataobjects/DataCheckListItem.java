package pl.otwartezabytki.android.dataobjects;

import android.os.Parcel;
import android.os.Parcelable;

public class DataCheckListItem implements Parcelable{

	long id;
	long relicID;			// fk - _id from TABLE_RELICS
	int questionID;
	int parentID;
	String parentAnswer;
	String question;
	String possibleAnswers;
	String answer;
	String mediaFilePath = "";
	
	public DataCheckListItem(long id, long relicID, int questionID, int parentID, String parentAnswer, String question, String possibleAnswers, String answer, String mediaFilePath){
		this.id = id;
		this.relicID = relicID;
		this.questionID = questionID;
		this.parentID = parentID;
		this.question = question;
		this.possibleAnswers = possibleAnswers;
		this.parentAnswer = parentAnswer;
		this.answer = answer;
		this.mediaFilePath = mediaFilePath;
		
	}
	
	public DataCheckListItem(Parcel source){
		id = source.readLong();
		relicID = source.readLong();
		questionID = source.readInt();
		parentID = source.readInt();
		parentAnswer = source.readString();
		question = source.readString();
		possibleAnswers = source.readString();
		answer = source.readString();
		mediaFilePath = source.readString();
	}
	
	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public long getRelic_id() {
		return relicID;
	}

	public void setRelic_id(long relic_id) {
		this.relicID = relic_id;
	}

	public String getMediaFilePath() {
		return mediaFilePath;
	}

	public void setMediaFilePath(String mediaFilePath) {
		this.mediaFilePath = mediaFilePath;
	}

	public int getQuestionID() {
		return questionID;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getParentID() {
		return parentID;
	}

	public String getParentAnswer() {
		return parentAnswer;
	}

	public String getQuestion() {
		return question;
	}

	public String getPossibleAnswers() {
		return possibleAnswers;
	}

	
	public long getRelicID() {
		return relicID;
	}

	public void setRelicID(long relicID) {
		this.relicID = relicID;
	}

	public void setQuestionID(int questionID) {
		this.questionID = questionID;
	}

	public void setParentID(int parentID) {
		this.parentID = parentID;
	}

	public void setParentAnswer(String parentAnswer) {
		this.parentAnswer = parentAnswer;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public void setPossibleAnswers(String possibleAnswers) {
		this.possibleAnswers = possibleAnswers;
	}

	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel destination, int flags) {
		destination.writeLong(id);
		destination.writeLong(relicID);
		destination.writeInt(questionID);
		destination.writeInt(parentID);
		destination.writeString(parentAnswer);
		destination.writeString(question);
		destination.writeString(possibleAnswers);
		destination.writeString(answer);
		destination.writeString(mediaFilePath);		
	}
	
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public DataCheckListItem createFromParcel(Parcel source) {
            return new DataCheckListItem(source); 
        }

        public DataCheckListItem[] newArray(int size) {
            return new DataCheckListItem[size];
        }
    };
}
