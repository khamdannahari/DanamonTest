package com.android.khamdan.data.user

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class UserRepository(private val userDao: UserDao) {

    fun getUserByEmail(email: String): Flow<User?> {
        return userDao.getUserByEmail(email)
    }

    fun getUserByEmailAndPassword(email: String, password: String): Flow<User?> {
        return userDao.getUserByEmailAndPassword(email, password)
    }

    fun getAllUsers(): Flow<List<User>> {
        return userDao.getAllUsers()
    }

    suspend fun insertUser(user: User): Flow<Result<Unit>> {
        return flow {
            userDao.getUserByUsername(user.username).collect { existingUserByUsername ->
                if (existingUserByUsername != null) {
                    emit(Result.failure(Exception("Username already exists")))
                    return@collect
                }

                userDao.getUserByEmail(user.email).collect { existingUserByEmail ->
                    if (existingUserByEmail != null) {
                        emit(Result.failure(Exception("Email already exists")))
                        return@collect
                    }

                    userDao.insertUser(user)
                    emit(Result.success(Unit))
                }
            }
        }.catch { e ->
            emit(Result.failure(e))
        }
    }
}
