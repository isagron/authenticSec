import {Component, OnDestroy, OnInit} from '@angular/core';
import {User} from '../../model/user.model';
import {NgModel} from '@angular/forms';
import {UserService} from '../../service/user.service';
import {Subscription} from 'rxjs';
import {Page} from '../../model/utils.model';
import {NgbModal, NgbModalOptions} from '@ng-bootstrap/ng-bootstrap';
import {NewUserModalComponent} from './new-user-modal/new-user-modal.component';
import {NotificationService} from '../../service/notification.service';

@Component({
  selector: 'app-users-management',
  templateUrl: './users-management.component.html',
  styleUrls: ['./users-management.component.css']
})
export class UsersManagementComponent implements OnInit, OnDestroy {

  userNameFilter: string;

  roleFilter: string;

  users: User[];

  pageSize = 10;
  totalNumberOfUsers = 0;
  pageArray: number[] = [];
  activePage = 1;
  activePrev = false;
  activeNext = false;

  private usersSubscription: Subscription;

  modalOptions: NgbModalOptions;


  constructor(private notificationService: NotificationService, private userService: UserService, private modalService: NgbModal) {
    this.modalOptions = {
      backdrop: 'static',
      backdropClass: 'customBackdrop',
      centered: true
    };
  }

  ngOnInit(): void {
    this.usersSubscription = this.userService.usersPage.subscribe(usersPage => {
      this.users = usersPage.content;
      this.handlePaging(usersPage);
    });
    this.getUsers();
  }

  openModal() {
    const modalRef = this.modalService.open(NewUserModalComponent, this.modalOptions);
    modalRef.result.then((result) => {
      console.log(result);
    });

  }

  private getUsers() {
    this.userService.getAllUsersPage(this.userNameFilter, this.roleFilter, this.activePage - 1, this.pageSize);
  }

  private handlePaging(usersPage: Page<User>) {
    const numberOfPages = Math.ceil(usersPage.totalElements / this.pageSize);
    this.totalNumberOfUsers = usersPage.totalElements;
    this.pageArray = Array(numberOfPages).fill(1);
    this.activePage = 1;
    this.activePrev = false;
    this.activeNext = usersPage.totalElements > usersPage.pageSize;
  }

  goToPage(pageNumber: number) {
    this.activePage = pageNumber;
    this.getUsers();
  }

  goToNext() {
    this.goToPage(this.activePage++);
  }

  goToPrev() {
    this.goToPage(this.activePage--);

  }

  ngOnDestroy(): void {
    if (!!this.usersSubscription) {
      this.usersSubscription.unsubscribe();
    }
  }


  deleteUser(user: User) {
    this.userService.reqDeleteUser(user.userId)
      .subscribe(response => {
        this.notificationService.showSuccessNotification('Delete user: ' + user.userName);
      });
  }

  editUser(user: User) {
    const modalRef = this.modalService.open(NewUserModalComponent, this.modalOptions);
    modalRef.componentInstance.user = user;
    modalRef.result.then((result) => {
      console.log(result);
    });
  }
}
