import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';

@Component({
  selector: 'app-security-questions',
  templateUrl: './security-questions.component.html',
  styleUrls: ['./security-questions.component.css']
})
export class SecurityQuestionsComponent implements OnInit {
  @Output() security = new EventEmitter<{ question: string, answer: string }>();
  @Input() confirmClicked: boolean;

  selectedSecurityQuestion1 = '';
  answer: string;

  securityQuestions = [
    ['what is your mother\'s maiden name?', 'something'],
    ['What is the name of your first pet?', ''],
    ['What was your first car?', ''],
    ['What elementary school did you attend?', ''],
    ['What is the name of the town where you were born?', ''],
    ['Name of your hometown?', ''],
    ['What is your favourite book?', ''],
    ['What is your favourite food?', ''],
    ['Where did you meet your spouse?', ''],
    ['Where do you like to vacation the most?', ''],
    ['What is your oldest sibling’s middle name?', ''],
    ['Who is your favourite superhero?', ''],
  ];

  constructor() { }

  ngOnInit(): void {
  }

  addSecurityQuestion(): void {
    this.security.emit({
      question: this.selectedSecurityQuestion1,
      answer: this.answer
    });
  }

}
