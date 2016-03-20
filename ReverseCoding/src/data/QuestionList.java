package data;

public class QuestionList {
	public boolean status;
	public String name;
	public Question questions[];
	
	public QuestionList(boolean status,Question questions[]) {
		this.status=status;
		this.questions=new Question[questions.length];
		for(int a=0;a<questions.length;a++)
			this.questions[a]=questions[a];
	}
}
