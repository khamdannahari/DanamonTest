package com.android.khamdan.data.user

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.take

class UserRepository(private val userDao: UserDao) {

    fun getAllUsers(): Flow<List<User>> {
        return userDao.getAllUsers()
    }

    suspend fun deleteUser(id: Long) {
        userDao.deleteUserById(id)
    }

    fun getUserByEmailAndPassword(email: String, password: String): Flow<User?> {
        return userDao.getUserByEmailAndPassword(email, password)
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
        }.catch { exception ->
            emit(Result.failure(exception))
        }.take(1)
            .flowOn(Dispatchers.IO)
    }

    suspend fun updateUser(user: User): Flow<Result<Unit>> {
        return flow {
            try {
                userDao.updateUser(user)
                emit(Result.success(Unit))
            } catch (exception: Exception) {
                emit(Result.failure(exception))
            }
        }
    }
}
